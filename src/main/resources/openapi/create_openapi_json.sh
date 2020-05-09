#!/bin/sh
# Please use this before creating OpenApi with codegen and use codegen on created ApiMerged.json
# USAGE (in e.g.: git-bash):
# ./create_openapi_json.sh PATH_TO_OUTPUT_PROJECT
# NOTE: if you don't specify any path this script will only merge the jsons

if which npm > /dev/null
    then
        package='swagger-cli'
        if [ `npm list -g | grep -c $package` -eq 0 ]; then
          # npm install -g $package --no-shrinkwrap
          echo "Error: missing package!"
          echo "Please install swagger-cli using following command:"
          echo "npm install -g $package"
          read -n 1 -s -r -p "Press any key to continue"
        else
          echo "Building OpenApi File"
          # Please use this output name (added to gitignore)
          output=$(swagger-cli bundle -r Api.json --outfile ApiMerged.json)
          if [ "$output" = "Created ApiMerged.json from Api.json" ]; then
            if [ -d "$1" ]
              then
                echo "Running Codegen with output at location: $1"
                java -jar ./openapi-generator-cli.jar generate -g spring -i ./ApiMerged.json -o "$1"
            else
              echo "Invalid path parameter!"
            fi
          else
            echo "Failed to merge OpenApi specs"
          fi
        fi
    else
        echo "Error - npm not found"
    fi