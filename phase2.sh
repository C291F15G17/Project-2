echo "Sorting Files..."
sort -u  -o scores.txt scores.txt
sort -u  -o pterms.txt pterms.txt
sort -u  -o rterms.txt rterms.txt

echo "Creating index files..."
cat reviews.txt | perl break.pl | db_load -c dupsort=1 -T -t hash rw.idx
cat scores.txt | perl break.pl | db_load -c dupsort=1 -T -t btree sc.idx
cat pterms.txt | perl break.pl | db_load -c dupsort=1 -T -t btree pt.idx
cat rterms.txt | perl break.pl | db_load -c dupsort=1 -T -t btree rt.idx