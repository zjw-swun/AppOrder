#version 300 es
precision mediump float;
in  vec4 vColor;
out vec4 fragColor;
out vec4 sss;
void main()
{
   //fragColor = vColor;
   sss = vColor;
}