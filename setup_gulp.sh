#!/bin/bash

npm_install_save() {
  plugin=$1
  if [ -e "./node_modules/$plugin" ]; then
    echo "$plugin is already exists"
  else
    npm install $plugin --save
  fi
}

npm_install_save gulp
npm_install_save gulp-riot
npm_install_save typescript-simple
npm_install_save gulp-concat
npm_install_save gulp-uglify
npm_install_save gulp-sass
npm_install_save run-sequence
