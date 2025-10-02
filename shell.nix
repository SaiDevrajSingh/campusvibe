{ pkgs ? import <nixpkgs> {} }:
pkgs.mkShell {
  buildInputs = [
    pkgs.jdk17
    pkgs.gradle
  ];

  shellHook = ''
    export JAVA_HOME=${pkgs.jdk17}/lib/openjdk
    export PATH=$JAVA_HOME/bin:${pkgs.gradle}/bin:$PATH
    echo "✅ Java + Gradle environment ready"
  '';
}
