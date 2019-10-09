#!/bin/sh
# ------------------------------------------
# create HTML pages by replacing #include directive
# in *.html files in $INPUT_DIR and placed files 
# of same name in output directory
# ------------------------------------------

function apply_gcc {
	echo $1 will be copied to $2
	gcc -P -x c -E $1 > $2
}

INPUT_DIR=${1:-../template}
OUTPUT_DIR=${2:-../public}

files_to_pocess=`ls -l1 $INPUT_DIR/*.html`

for f in $files_to_pocess; do
	file_name=`basename $f`
	apply_gcc $f $OUTPUT_DIR/$file_name
done
	

