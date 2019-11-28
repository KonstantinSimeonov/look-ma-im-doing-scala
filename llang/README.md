## LLang (LameLanguage)
- supports only non-negative integer numbers
- only mutable local variables of int type
- the following instructions:
  - zero(x) - sets x to 0
  - incr(x) - sets x to x + 1
  - loop(x) - repeats the body of the loop x times
  - asgn(x, y) - copy y into x
- supports declaration of routines that can accept parameters
- no goto, if, return, operators and number literals
- routines can be reused across questions

Example:
```
// swap routine
swap(x, y) {
    asgn(tmp, x) // local variables are declated on first use
    asgn(x, y)
    asgn(y, tmp)
}
```

1. implement `decr(x)`, which would set the value of `x` to `x - 1`. If `x` is 0, then the result will also be 0.

```
// x = 3, decr(x) => x = 2
// x = 5, decr(x) => x = 4
// x = 0, decr(x) => x = 0
decr(x) {
    zero(v1)
    loop(x) {
        asgn(r, __)
        incr(__)
    }
    asgn(x, r)
}
```

1. implement `subt(x, y)` which subtracts `y` from `x` and sets `x` to the result.

```
// x = 10, y = 3, subt(x, y) => x = 7
// x = 5, y = 10, subt(x, y) => x = 0
subt(x, y) {

}
```

1. implement
    - `add(x, y)` - adds `x` and `y` into `x`
    - `decr(x)` - subtract 1 from `x`
    - `mult(x, y)` - multiplies `x` and `y` into `x`
    - `div(x, y, q, r)` - divides `x` by `y` and writes the quotient into `q` and the remainder into `r`
    - `comp(x, y, c)` - write 0 into `c` if `x` is equal to `y` and a non-zero value otherwise
    - `gt(x, y, g)` - write 1 into `g` if `x > y` and 0 otherwise
    - `ge(x, y, g)` - write 1 into `g` if `x >= y` and 0 otherwise
    - `iszero(x, z)` - write 1 into `z` if `x` is equal to 0, and 0 otherwise
