{
  description = "please modify your flake description here";

  inputs = {
    nixpkgs.follows = "euphgh/nixpkgs";
    euphgh.url = "github:euphgh/flakes";
  };

  outputs = { nixpkgs, ... }@inputs: {
    devShells =
      let
        # default system enum
        defaultSysList = [ "aarch64-darwin" "aarch64-linux" "x86_64-darwin" "x86_64-linux" ];

        # make devShell or package
        foreachSysInList = sys: f: nixpkgs.lib.genAttrs (sys) (system:
          f {
            nixpkgs = nixpkgs.legacyPackages.${system};
            devShells = inputs.euphgh.devShells.${system};
            packages = inputs.euphgh.packages.${system};
          });
      in
      foreachSysInList defaultSysList ({ nixpkgs, devShells, packages, ... }: {
        default =
          let
            millw = packages.millw.override { alias = "mill"; };
          in
          nixpkgs.mkShell {
            packages = (with nixpkgs; [
              jdk17_headless
              millw
            ]);
          };
      });
  };
}
