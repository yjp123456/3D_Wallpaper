precision mediump float;
varying vec3 v_Color;
varying float v_ElapsedTime;
uniform sampler2D u_TextureUnit;

void main(){
   gl_FragColor = vec4(v_Color / v_ElapsedTime, 1.0) * texture2D(u_TextureUnit, gl_PointCoord);
   //gl_PointCoord代表纹理上每个像素点的坐标，x,y范围是[0,1]，如果当前图元不是点那么这个值是未定义的
   //传入这个值后着色器会从纹理的左上角坐标开始遍历每个像素，这样就能在点上面绘制出一张纹理图了
   /*float xDistance = 0.5 - gl_PointCoord.x;
   float yDistance = 0.5 - gl_PointCoord.y;//自定义粒子外形为圆形,圆心为（0.5,0.5），x,y范围都为[0,1]
   float distanceFromCenter = sqrt(xDistance * xDistance + yDistance * yDistance);

   if (distanceFromCenter > 0.5) {
       discard;
   } else {
       gl_FragColor = vec4(v_Color / v_ElapsedTime, 1.0);
   }*/
}
