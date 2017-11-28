uniform mat4 u_Matrix;

attribute vec4 a_Position;
attribute vec2 a_TextureCoordinates;

varying vec2 v_TextureCoordinates;//varying类型可以用来传输数据，即vertex赋值，fragment使用这个值

void main(){
   v_TextureCoordinates = a_TextureCoordinates;
   gl_Position = u_Matrix * a_Position;
   gl_PointSize = 10.0;
}