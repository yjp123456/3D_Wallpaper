precision mediump float;

uniform samplerCube u_TextureUnit;
varying vec3 v_Position;

void main(){
  gl_FragColor = textureCube(u_TextureUnit, v_Position);//立方体纹理坐标相当于从原点看过去的方向向量
}