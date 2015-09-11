
import os;

for file in os.listdir('/home/jpjiang/Data/wikidump'):
    pathsrc = '/home/jpjiang/Data/wikidump' + file;
    pathout = '/home/jpjiang/Data/wikidump_index' + file;
    print 'qsub /home/jpjiang/Data/build_index_wikidump.sh %s %s' % ( pathsrc, pathout );

