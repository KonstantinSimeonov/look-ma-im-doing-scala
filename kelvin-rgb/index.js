const cssFromRgb = rgb => `rgb(${rgb.map(Math.floor)})`;
const showRgb = rgb => `[${rgb.map(Math.floor)}]`;

const appr = document.querySelector('#approxes');

xs.forEach(([rgb, kelvin, krgb]) => {
	const rgbDiv = `<div class="swatch" style="background: ${cssFromRgb(rgb)}"></div>`;
	const krgbDiv = `<div class="swatch" style="background: ${cssFromRgb(krgb)}"></div>`;
	appr.innerHTML += `<tr class="swatch-container">
<td>${rgbDiv}</td>
<td>${krgbDiv}</td>
<td>&nbsp; K: ${kelvin}</td>
<td><span class="approx">[${krgb.map(Math.round)}]</span></td>
<td><span class="source">[${rgb.map(Math.round)}]</source></td>
</tr>`;
});
