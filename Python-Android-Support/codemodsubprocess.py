import sys

def main():
    filenames = sys.argv[1:]
    for filename in filenames:
        print(filename)

if __name__ == '__main__':
    main()