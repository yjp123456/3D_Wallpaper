uniform mat4 u_MVMatrix;//modelViewMatrix
uniform mat4 u_IT_MVMatrix;//transpose of inverse of modelVewMatrix
uniform mat4 u_MVPMatrix;//modelViewProjectionMatrix

uniform vec3 u_VectorToLight;//In eye space
uniform vec4 u_PointLightPositions[3];//In eye space
uniform vec3 u_PointLightColors[3];

attribute vec4 a_Position;
attribute vec3 a_Normal;

varying vec3 v_Color;

vec3 materialColor;
vec4 eyeSpacePosition;
vec3 eyeSpaceNormal;

vec3 getAmbientLighting();
vec3 getDirectionalLighting();
vec3 getPointLighting();

void main(){
  materialColor = mix(vec3(0.180, 0.467, 0.153),//a dark green
                vec3(0.660, 0.670, 0.680),//a stony gray
                a_Position.y);
  eyeSpacePosition = u_MVMatrix * a_Position;

 //利用逆矩阵的转置矩阵消除缩放和视角带来的影响（逆矩阵是考虑缩放，转置目的是考虑平移旋转），和之前乘于宽高缩放比倒数效果一样为了保持形状
  //之所以不直接乘于modelViewMatrix，是因为如果矩阵中有缩放且各个方向缩放比例不一样的话就不符合要求了
  eyeSpaceNormal = normalize(vec3(u_IT_MVMatrix * vec4(a_Normal, 0.0)));

  v_Color = getAmbientLighting();
  v_Color += getDirectionalLighting();
  v_Color += getPointLighting();

   /*vec3 scaleNormal = a_Normal;
   scaleNormal.y *= 10.0;//取缩放后宽高比的倒数，为了防止形状被改变，乘数一定要是浮点数

   scaleNormal = normalize(scaleNormal);//归一化，每个分量除以模长
   float diffuse = max(dot(scaleNormal,u_VectorToLight),0.0);//计算平面与光线向量点积
   v_Color *= diffuse;
   float ambient = 0.2;
   v_Color += ambient;//防止过暗  */
   gl_Position = u_MVPMatrix * a_Position;
}

vec3 getAmbientLighting(){
  return materialColor * 0.7;
}

vec3 getDirectionalLighting(){
  return materialColor * 0.3 * max(dot(eyeSpaceNormal, u_VectorToLight), 0.0);
}

vec3 getPointLighting(){
  vec3 lightingSum = vec3(0.0);

  for(int i = 0;i < 3; i++){
    //计算每个光点到当前视角位置的距离
    vec3 toPointLight = vec3(u_PointLightPositions[i]) - vec3(eyeSpacePosition);
    float distance = length(toPointLight);
    toPointLight = normalize(toPointLight);

    float cosine = max(dot(eyeSpaceNormal, toPointLight), 0.0);
    lightingSum += (materialColor * u_PointLightColors[i] * 2.0 * cosine) / distance;//5是用于调节亮度的
  }

  return lightingSum;
}