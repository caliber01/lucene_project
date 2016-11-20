import re
from os import listdir, path
from sys import argv

def fix_file(filename):
    name, extension = path.splitext(filename)
    substitutions = {
            '(&)(?!.{0,10};)': '$amp;',
            '&mdash;': '&#8212;',
            '&deg;': '&#176;'
    }
    with open(name + extension, 'r+') as content_file:
        content = content_file.read()
        substituted = content
        for pattern, replacement in substitutions.items():
            substituted = re.sub(pattern, replacement, substituted)

        content_file.seek(0)
        content_file.write(substituted)
        content_file.truncate()


def fix_files(directory):
    for filename in listdir(directory):
        if filename.endswith('.xml'):
            print('fixing ' + filename)
            fix_file(path.join(directory, filename))
    print('success')


fix_files(argv[1])


