uniform mat4 u_Matrix;
attribute vec3 a_Position;
varying vec3 v_Position;

void main(){
  v_Position = a_Position;
  v_Position.z = -v_Position.z;//立方体纹理坐标的z和实际的是相反的，所以需要处理一下

  gl_Position  = u_Matrix * vec4(a_Position, 1.0);
  gl_Position = gl_Position.xyww;
  //z经过perspective后变成1，所以总在所有事物的后面(NDC是左手坐标系)
  //默认情况下，在片源着色器中gl_FragCoord.z的深度取值范围是[0, 1]，而NDC坐标系中的z的取值范围是[-1, 1]
  //OpenGL会根据函数glDepthRange(nearVal, farVal)中nearVal与farVal的值，将NDC坐标系中z从[-1, 1]映射到[nearVal, farVal]
}