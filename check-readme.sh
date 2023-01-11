./gradlew :readme:run > /dev/null

if [[ $(git status --porcelain) ]]; then
    echo "there are unstaged changes after running :readme:run:" 1>&2
    git status --porcelain 1>&2
    exit 1
fi
