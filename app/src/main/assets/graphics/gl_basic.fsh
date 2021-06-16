precision mediump float;

uniform sampler2D texture;
uniform vec4 tintColor;

varying vec2 texPos;

void main() {

  gl_FragColor = texture2D(texture, texPos) * tintColor;

}