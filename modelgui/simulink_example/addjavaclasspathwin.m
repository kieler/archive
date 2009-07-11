function addjavaclasspathwin
%
%

% Tinyline:
%javaaddpath('u:\workspaces\eclipse\modelgui\TinyLine\bin')
%javaaddpath('u:\workspaces\eclipse\modelgui\TinyLine\lib')

% Batik:
prefix = 'u:\shared\modelgui\trunk\batik-1.6\';
%javaaddpath('u:\workspaces\eclipse\modelgui\batik-1.6\bin')
javaaddpath(strcat(prefix,'bin'));
javaaddpath(strcat(prefix,'lib\batik-awt-util.jar'))
javaaddpath(strcat(prefix,'lib\batik-bridge.jar'))
javaaddpath(strcat(prefix,'lib\batik-css.jar'))
javaaddpath(strcat(prefix,'lib\batik-dom.jar'))
javaaddpath(strcat(prefix,'lib\batik-extension.jar'))
javaaddpath(strcat(prefix,'lib\batik-ext.jar'))
javaaddpath(strcat(prefix,'lib\batik-gui-util.jar'))
javaaddpath(strcat(prefix,'lib\batik-gvt.jar'))
javaaddpath(strcat(prefix,'lib\batik-parser.jar'))
javaaddpath(strcat(prefix,'lib\batik-script.jar'))
javaaddpath(strcat(prefix,'lib\batik-svg-dom.jar'))
javaaddpath(strcat(prefix,'lib\batik-svggen.jar'))
javaaddpath(strcat(prefix,'lib\batik-swing.jar'))
javaaddpath(strcat(prefix,'lib\batik-transcoder.jar'))
javaaddpath(strcat(prefix,'lib\batik-util.jar'))
javaaddpath(strcat(prefix,'lib\batik-xml.jar'))
javaaddpath(strcat(prefix,'lib\js.jar'))
javaaddpath(strcat(prefix,'lib\pdf-transcoder.jar'))
javaaddpath(strcat(prefix,'lib\xerces_2_5_0.jar'))
javaaddpath(strcat(prefix,'lib\xml-apis.jar'))
