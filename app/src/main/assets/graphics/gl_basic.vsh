attribute vec2 av_Position;
attribute vec2 af_Position;

uniform vec2 av_Offset;
uniform float ratio;
uniform vec3 viewport;
uniform mat2 rotationMatrix;

varying vec2 texPos;

void main() {

    gl_Position = vec4(((rotationMatrix * av_Position + av_Offset) * vec2(1.0, ratio) + viewport.rg) * viewport.b, 1.0, 1.0);
    texPos = af_Position;

}