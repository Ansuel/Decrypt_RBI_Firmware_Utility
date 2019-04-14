last_log="$(git log --oneline -n 1)"

if [ "$(echo "$last_log" | grep -o "\[[0-9]\+\.[0-9]\+\]" | tr -d [ | tr -d ])" ]; then
	VER="$(echo "$last_log" | grep -o "\[[0-9]\+\.[0-9]\+\]" | tr -d [ | tr -d ])"
	echo "Detected manual version: "$manual_ver
else
	VER=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)
fi

SHORT_GIT_HASH=$(echo $CIRCLE_SHA1 | cut -c -7)

mkdir ~/decrypt-rbi/target
echo "$VER"-"$SHORT_GIT_HASH" > ~/decrypt-rbi/target/version.txt

mvn versions:set -DnewVersion="$VER"-"$SHORT_GIT_HASH"
mvn versions:commit