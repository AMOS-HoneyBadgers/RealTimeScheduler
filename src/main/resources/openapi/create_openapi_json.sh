#!/bin/sh
# Please use this before creating OpenApi with codegen and use codegen on created ApiMerged.json
# Please do not change output name
if which npm > /dev/null
    then
        package='swagger-cli'
        if [ `npm list -g | grep -c $package` -eq 0 ]; then
          # npm install -g $package --no-shrinkwrap
          echo "Error: missing package!"
          echo "Please install swagger-cli using following command:"
          echo "npm install -g $package"
        else
          echo "Building OpenApi File"
          # Please use this output name (added to gitignore)
          swagger-cli bundle -r Api.json --outfile ApiMerged.json
        fi
    else
        echo "Error - npm not found"
    fi