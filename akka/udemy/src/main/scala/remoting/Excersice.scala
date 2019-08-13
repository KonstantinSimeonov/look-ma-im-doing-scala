package excercise

import scala.io.Source
import com.typesafe.config.ConfigFactory
import akka.actor.{
  Actor,
  ActorSystem,
  Props,
  Identify,
  ActorLogging,
  ActorRef,
  PoisonPill,
  ActorIdentity
}

object WordCountDomain {
  case class Initialize(workerCount: Int)
  case class WordCountTask(text: String)
  case class WordCountResult(count: Int)
}

class WordCountWorker extends Actor with ActorLogging {
  import WordCountDomain._

  override def receive: Receive = {
    case WordCountTask(text) => {
      this.log.info(s"Processing: $text")
      this.sender() ! WordCountResult(text.split(" ").length)
    }
  }
}

class WordCountMaster extends Actor with ActorLogging {
  import WordCountDomain._

  override def receive: Receive = {
    case Initialize(workerCount) => {
      (1 to workerCount).foreach(id => {
        val selection = this.context.actorSelection(
          s"akka://WorkerSystem@localhost:2552/user/bachkator$id"
        )

        selection ! Identify("kiro")
      })

      this.context.become(initializing(List(), workerCount))
    }
  }

  def initializing(workers: List[ActorRef], remainingWorkersCount: Int): Receive = {
    case ActorIdentity("kiro", Some(workerRef)) => {
      val newWorkers = workerRef :: workers
      val newReceive = 
        if (remainingWorkersCount == 1) {
          this.log.info("GOING ONLINE BOI")
          online(newWorkers, 0, 0)
        } else {
          this.log.info(s"ACTOR BOI JUST CHECKED IN: ${workerRef.path.name}")
          initializing(newWorkers, remainingWorkersCount - 1)
        }

      this.context.become(newReceive)
    }
  }

  def online(workers: List[ActorRef], remainingTasks: Long, totalCount: Long): Receive = {
    case text: String => {
      val sentences = text.split("\\. ")
      Iterator
        .continually(workers)
        .flatten
        .zip(sentences.iterator)
        .foreach {
          case (worker, sentence) => worker ! WordCountTask(sentence)
        }

      context.become(online(workers, remainingTasks + sentences.length, totalCount))
    }

    case WordCountResult(count) =>
      if (remainingTasks == 1) {
        this.log.info(s"TOTAL: ${totalCount + count}")
        workers.foreach(_ ! PoisonPill)
        this.context.stop(self)
      } else {
        context.become(online(workers, remainingTasks - 1, totalCount + count))
      }
  }
}

object AppConfig {
  val actorCount = 5
}

object MasterApp {
  def main(args: Array[String]): Unit = {
    import WordCountDomain._
    val config = ConfigFactory.parseString(
      """
        |akka.remote.artery.canonical.port = 2551
      """.stripMargin)
        .withFallback(ConfigFactory.load("remoting/excercise.conf"))
  
    val system = ActorSystem("MasterSystem", config)
  
    val master = system.actorOf(Props[WordCountMaster], "shefe")
    master ! Initialize(AppConfig.actorCount)
    Thread.sleep(1000)
  
    Source
      .fromFile("src/main/resources/txt/lipsum.txt")
      .getLines
      .foreach(master ! _)
    }
}

object WorkersApp {
  def main(args: Array[String]): Unit = {
    import WordCountDomain._
    val config = ConfigFactory.parseString(
      """
        |akka.remote.artery.canonical.port = 2552
      """.stripMargin)
        .withFallback(ConfigFactory.load("remoting/excercise.conf"))

    println(config)
  
    val system = ActorSystem("WorkerSystem", config)
  
    for (i <- 1 to AppConfig.actorCount) {
      system.actorOf(Props[WordCountWorker], s"bachkator$i")
    }
  }
}
