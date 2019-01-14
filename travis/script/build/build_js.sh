#!/bin/bash -e

[ ! -z "$DEBUG_BASH" ] && set -x
cd /home/ci/saros_src

ui_package_json="saros.ui.frontend/html/package.json"
if [ -f "$ui_package_json" ]; then
  cd saros.ui.frontend/html
  npm cache verify
  npm install
  npm run lint
  npm run build
  npm run test:log
fi
