"""Updater
Updates Stream Pi

Nicholas Tindle Jan 1 2020
"""
import os
import pathlib
import random
import shutil
import string
import zipfile

def update():
    """update
    Updates the Stream Pi

    Steps: 
    1. The python file will search for a file named "update.zip" in the current directory.
    2. It will unzip the update zip in a directory named 'unzip'. 
    3. It will delete everything from the parent directory's parent (../)
    4. Copy the extracted contents from the 'unzip' directory to the parent directory's parent (../), except the folder "updater"
    5. Delete temporary directory
    6. Delete "update.zip"

    Nicholas Tindle Jan 1 2020
    """
    # if we find an update
    print(os.getcwd())
    if 'update.zip' in os.listdir():
        print('update.zip found')
        # Save update.zip as a pathlib path
        update = pathlib.Path('update.zip').resolve()
        print("update found: {}".format(update))

        tempDir = pathlib.Path().resolve()

        # Unzip the update
        with zipfile.ZipFile(update, 'r') as zip_ref:
            zip_ref.extractall()
        print('Zip Extracted')

        # move temp dir into correct loc
        tempDir = tempDir / 'update'

        # Get and resolve the parent directory
        parentDir = pathlib.Path(os.getcwd()).resolve().parent

        # Delete everything in parent directory except updater
        for c in parentDir.iterdir():
            if c.name != 'updater':
                if c.is_dir():
                    print(str(c) + ' is dir')
                    shutil.rmtree(c)
                if c.is_file():
                    print(str(c) + ' is file')
                    c.unlink()
        # Move all the files from temp up to parent
        for c in tempDir.iterdir():
            if c.name != "updater":
                print("source: {} dest: {}".format(c.name, parentDir.name))
                shutil.move(str(c.absolute()), parentDir)
        print('Removing {}'.format(tempDir.parent))
        shutil.rmtree(tempDir)
        
        print("Removing update.zip")
        update.unlink()



if __name__ == "__main__":
    update()