#!/usr/bin/perl
# Written by Evangelos Kanoulas
# Last update October 24, 2011
 
# Session Track 2011 Evaluation Script
# Input : qrel, runs, mapping between <current query> and subtopics
# Output : a set of measures including err, ndcg, gap, ap, and pc

use Getopt::Long;

my $qrels;
my $runs = "";
my $alltopic = 0;
my $lastq = 0;
my $mapdr = "";
my $k = 10;
my $dups = 0;

GetOptions('qrels=s', \$qrels,
	   'runs=s', \$runprefix,
	   'q=i', \$alltopic,
	   's=i', \$lastq,
	   'm=s', \$mapdr,
	   'k=i', \$k,
           'dups=i',\$dups);


if (!$qrels)
{
die("Usage:  session_eval.pl -qrels <qrels>
Options:  -runs <path to runs; default cwd>
          -q <0/1; default 0, use 1 to print info for all topics>
          -s <0/1; default 0, use 1 to compute relevance only considering the subtopics relevant to the last query>
          -m <path to sessionlastquery_subtopic_map.txt file; default cwd>
          -k <rank cutoff; default 10>");
}


# QRELS
my (%ngrades_topic, %ngrades_subtopic, %ngrades_session, %rel_topic, %rel_subtopic, %rel_session);
open Q, $qrels;
while (<Q>)
{
	chop;
	# each row in the qrel consists of:
	# topic subtopic document relevance
        # relevance in {-2,0,1,2,3}
	my @i = split(/\s+/);
	
	$rel_subtopic{$i[0]}{$i[1]}{$i[2]} = $i[3];
	$ngrades_subtopic{$i[0]}{$i[1]}{$i[3]}++;
}
close Q;

# For each (topic,document) pair if a document is relevant to more than one subtopics of that topic then 
# consider the maximum relevance grade as its relevance.
foreach $t (keys %rel_subtopic){
    foreach $subt (keys %{$rel_subtopic{$t}}){
	foreach $d (keys %{$rel_subtopic{$t}{$subt}}){
	    if (exists $rel_topic{$t}{$d}){
		$rel_topic{$t}{$d}=$rel_subtopic{$t}{$subt}{$d} if ($rel_topic{$t}{$d}<$rel_subtopic{$t}{$subt}{$d});}
	    else{$rel_topic{$t}{$d}=$rel_subtopic{$t}{$subt}{$d};}
	}
    }
}
foreach $t (keys %rel_topic){
    foreach $d (keys %{$rel_topic{$t}}){
	$ngrades_topic{$t}{$rel_topic{$t}{$d}}++;
    }
}



# Mapping between the <current query> of a session and the relevant to it subtopics
open M, $mapdr."sessionlastquery_subtopic_map.txt";
my %mapping=();
my %session_topic = ();
while (<M>){
    chop;
    # each row in the sessionlastquery_subtopic_map file consists of: 
    # session topic subtopic
    my @i = split(/\s+/);
    $mapping{$i[0]}{$i[1]}{$i[2]}=1;
    $session_topic{$i[0]}=$i[1];
}
close M;

# For each (session,document) pair if a document is relevant to more than one subtopics of the ones 
# relevant to the <current query> consider the maximum relevance grade as its relevance.
foreach $ses (sort {$a <=> $b} keys %mapping){
    foreach $t (keys %{$mapping{$ses}}){
	# Iterate only over the subtopics that are relevant to the current query of the session
	foreach $subt (sort {$a <=> $b} keys %{$mapping{$ses}{$t}}){
	    foreach $d (keys %{$rel_subtopic{$t}{$subt}}){
		if (exists $rel_session{$ses}{$d}){
		    $rel_session{$ses}{$d}=$rel_subtopic{$t}{$subt}{$d} if ($rel_session{$ses}{$d}<$rel_subtopic{$t}{$subt}{$d});}
		else{$rel_session{$ses}{$d}=$rel_subtopic{$t}{$subt}{$d};}
	    }
	}
    }
}
foreach $ses (keys %rel_session){
    foreach $d (keys %{$rel_session{$ses}}){
	$ngrades_session{$ses}{$rel_session{$ses}{$d}}++;
    }
}





# Expected number of relevant documents for Graded Average Precision.
# (a) when the relevance is computed over all subtopics
my @g=(1/3,1/3,1/3);
my %gR=();
foreach my $t (sort {$a <=> $b} keys %ngrades_topic){
    my $sumg=0;
    for (my $i=1;$i<=scalar(@g);$i++){
	$sumg += $g[$i-1];
	$gR{$t} += $ngrades_topic{$t}{$i}*$sumg;
    }
}
# (b) when the relevance is computed over the subtopics relevant to the <current query>
my %gR_session=();
foreach my $s (sort {$a <=> $b} keys %ngrades_session){
    my $sumg=0;
    for (my $i=1;$i<=scalar(@g);$i++){
	$sumg += $g[$i-1];
	$gR_session{$s} += $ngrades_session{$s}{$i}*$sumg;
    }
}

# Discounted cumulative gain of the ideal ranking
# (a) when the relevance is computed over all subtopics
my %idcg=();
my %idcgk=();
foreach my $t (sort {$a <=> $b} keys %ngrades_topic){
    my $i;
    $idcg{$t} = 0;
    $idcgk{$t} = 0;
    for ($i=1; $i<=$ngrades_topic{$t}{3}; $i++){
	$idcg{$t} += (2**3-1)/(log($i+1)/log(2));
	$idcgk{$t} += (2**3-1)/(log($i+1)/log(2)) if($i<=$k);
    }
    for ($i; $i<=$ngrades_topic{$t}{3}+$ngrades_topic{$t}{2}; $i++){
	$idcg{$t} += (2**2-1)/(log($i+1)/log(2));
	$idcgk{$t} += (2**2-1)/(log($i+1)/log(2)) if($i<=$k);
    }
    for ($i; $i<=$ngrades_topic{$t}{3}+$ngrades_topic{$t}{2}+$ngrades_topic{$t}{1}; $i++){
	$idcg{$t} += (2**1-1)/(log($i+1)/log(2));
	$idcgk{$t} += (2**1-1)/(log($i+1)/log(2)) if($i<=$k);
    }
}
# (b) when the relevance is computed over the subtopics relevant to the <current query>
my %idcg_session=();
my %idcgk_session=();
foreach my $s (sort {$a <=> $b} keys %ngrades_session){
    my $i;
    $idcg_session{$s} = 0;
    $idcgk_session{$s} = 0;
    for ($i=1; $i<=$ngrades_session{$s}{3}; $i++){
	$idcg_session{$s} += (2**3-1)/(log($i+1)/log(2));
	$idcgk_session{$s} += (2**3-1)/(log($i+1)/log(2)) if($i<=$k);
    }
    for ($i; $i<=$ngrades_session{$s}{3}+$ngrades_session{$s}{2}; $i++){
	$idcg_session{$s} += (2**2-1)/(log($i+1)/log(2));
	$idcgk_session{$s} += (2**2-1)/(log($i+1)/log(2)) if($i<=$k);
    }
    for ($i; $i<=$ngrades_session{$s}{3}+$ngrades_session{$s}{2}+$ngrades_session{$s}{1}; $i++){
	$idcg_session{$s} += (2**1-1)/(log($i+1)/log(2));
	$idcgk_session{$s} += (2**1-1)/(log($i+1)/log(2)) if($i<=$k);
    }
}

# Expected reciprocal rank of the ideal ranking
# (a) when the relevance is computed over all subtopics
my %ierr=();
my %ierrk=();
foreach my $t (sort {$a <=> $b} keys %ngrades_topic){
    my $i;
    $ierr{$t} = 0;
    $ierrk{$t} = 0;
    my $p = 1;
    for ($i=1; $i<=$ngrades_topic{$t}{3}; $i++){
	my $R=(2**3-1)/(2**3);
	$ierr{$t} += $p*$R/$i;
	$ierrk{$t} += $p*$R/$i if($i<=$k);
	$p = $p*(1-$R);
    }
    for ($i; $i<=$ngrades_topic{$t}{3}+$ngrades_topic{$t}{2}; $i++){
	my $R=(2**2-1)/(2**3);
	$ierr{$t} += $p*$R/$i;
	$ierrk{$t} += $p*$R/$i if($i<=$k);
	$p = $p*(1-$R);
    }
    for ($i; $i<=$ngrades_topic{$t}{3}+$ngrades_topic{$t}{2}+$ngrades_topic{$t}{1}; $i++){
	my $R=(2**1-1)/(2**3);
	$ierr{$t} += $p*$R/$i;
	$ierrk{$t} += $p*$R/$i if($i<=$k);
	$p = $p*(1-$R);
    }
}
# (b) when the relevance is computed over the subtopics relevant to the <current query>
my %ierr_session=();
my %ierrk_session=();
foreach my $s (sort {$a <=> $b} keys %ngrades_session){
    my $i;
    $ierr_session{$s} = 0;
    $ierrk_session{$s} = 0;
    my $p = 1;
    for ($i=1; $i<=$ngrades_session{$s}{3}; $i++){
	my $R=(2**3-1)/(2**3);
	$ierr_session{$s} += $p*$R/$i;
	$ierrk_session{$s} += $p*$R/$i if($i<=$k);
	$p = $p*(1-$R);
    }
    for ($i; $i<=$ngrades_session{$s}{3}+$ngrades_session{$s}{2}; $i++){
	my $R=(2**2-1)/(2**3);
	$ierr_session{$s} += $p*$R/$i;
	$ierrk_session{$s} += $p*$R/$i if($i<=$k);
	$p = $p*(1-$R);
    }
    for ($i; $i<=$ngrades_session{$s}{3}+$ngrades_session{$s}{2}+$ngrades_session{$s}{1}; $i++){
	my $R=(2**1-1)/(2**3);
	$ierr_session{$s} += $p*$R/$i;
	$ierrk_session{$s} += $p*$R/$i if($i<=$k);
	$p = $p*(1-$R);
    }
}





# RUNS

my (%sumr, %ap, %sumgr, %gap, %dcg, %dcgk, %err, %errk, %nerr, %nerrk, %pc);

my @runs = glob("$runprefix*RL1");

my @RLs = ("RL1","RL2","RL3","RL4");

foreach my $RL (@RLs){
    
    foreach my $r (@runs)
    {
	(my $base = $r) =~ s/\.RL1//;
	$base = $base.".".$RL;

	my %rank = ();
	my %p = ();
	open F, "$base" || die "the file $base does not exist";
	while (<F>)
	{
	    chop;
	    # each row in the run file consists of:
	    # session Q0 document rank score name
	    my @i = split(/\s+/);
	    my $topic = $session_topic{$i[0]};
	    
	    #next if (!exists $rel_topic{$topic}{$i[2]});
	    #print "$topic \"$i[2]\" -3\n" if (!exists $rel_topic{$topic}{$i[2]});
	    #$rel = 0 if (!exists $rel_topic{$topic}{$i[2]});

            # if $lastq==1 then relevance is computed only on the subtopics related to the last query, 
            # i.e. <current query>, in the session; thus we obtain it from %rel_session
            # o.w. it is computed on all subtopics and thus we obtain it from %rel_topic
	    my $rel;
	    if ($lastq){
		if (exists $rel_session{$i[0]}{$i[2]}){
		    $rel = $rel_session{$i[0]}{$i[2]};}
		else{
		    $rel = 0;}
	    }
	    else{
		if (exists $rel_topic{$topic}{$i[2]}){
		    $rel = $rel_topic{$topic}{$i[2]};}
		else{
		    $rel = 0;}
	    }
	    	    
	    # Initialise the measures
	    if (!exists $sumr{$base}{$i[0]}){
		$sumr{$base}{$i[0]}=0;
		$ap{$base}{$i[0]}=0;
		$sumgr{$base}{$i[0]}{1}=0;
		$sumgr{$base}{$i[0]}{2}=0;
		$sumgr{$base}{$i[0]}{3}=0;
		$gap{$base}{$i[0]}=0;
		$dcg{$base}{$i[0]}=0;
		$dcgk{$base}{$i[0]}=0;
		$err{$base}{$i[0]}=0;
		$errk{$base}{$i[0]}=0;
		$pc{$base}{$i[0]}=0;
	    }
	    

	    if (exists $rank{$base}{$i[0]}){
		$rank{$base}{$i[0]}++;}
	    else{$rank{$base}{$i[0]}=1;}

	    # Probability the user stops at the current position
	    if (exists $p{$base}{$i[0]}){
		$p{$base}{$i[0]} = $p{$base}{$i[0]}*(1-$R);}
	    else{$p{$base}{$i[0]} = 1;}

	    $rel=0 if($rel<0);

	    # Computation of graded average precision
	    if ($rel>0){
		my $tmp=0;
		for (my $j=1;$j<=$rel;$j++){
		    $sumgr{$base}{$i[0]}{$j}++;
		}
		for (my $j=1;$j<=$rel;$j++){
		    $tmp += $sumgr{$base}{$i[0]}{$j}*$g[$j-1];
		}
		$gap{$base}{$i[0]} += $tmp/$rank{$base}{$i[0]};
	    }


	    # Computation of average precision
	    if ($rel>0){
		$sumr{$base}{$i[0]}++;
		$ap{$base}{$i[0]} += $sumr{$base}{$i[0]}/$rank{$base}{$i[0]};
	    }
	    
	    # Computation of precision at k
	    $pc{$base}{$i[0]} = $sumr{$base}{$i[0]}/$k if ($rank{$base}{$i[0]}<=$k);

	    # Computation of discounted cumulative gain
	    $dcg{$base}{$i[0]} += (2**$rel-1)/(log($rank{$base}{$i[0]}+1)/log(2));
	    $dcgk{$base}{$i[0]} += (2**$rel-1)/(log($rank{$base}{$i[0]}+1)/log(2)) if ($rank{$base}{$i[0]}<=$k);
	    
	    # Computation of expected reciprocal rank
	    $R=(2**$rel-1)/(2**3);
	    $err{$base}{$i[0]} += $p{$base}{$i[0]}*$R/$rank{$base}{$i[0]};
	    $errk{$base}{$i[0]} += $p{$base}{$i[0]}*$R/$rank{$base}{$i[0]} if ($rank{$base}{$i[0]}<=$k);
	}
	close F;
    }	
}    
    

my $nq; # Number of sessions with non-zero relevant documents
# Normalisation of the measures
foreach my $r (sort {$a <=> $b} keys %ap)
{
    $nq = 0;
    if ($lastq){
	
	foreach my $s (sort {$a <=> $b} keys %{$ap{$r}}){
	    next if ($ngrades_session{$s}{1}+$ngrades_session{$s}{2}+$ngrades_session{$s}{3} == 0);  # skip if no rels?
	    $ap{$r}{$s}=$ap{$r}{$s}/($ngrades_session{$s}{1}+$ngrades_session{$s}{2}+$ngrades_session{$s}{3});
	    $dcg{$r}{$s}=$dcg{$r}{$s}/$idcg_session{$s};
	    $dcgk{$r}{$s}=$dcgk{$r}{$s}/$idcgk_session{$s};
	    $gap{$r}{$s}=$gap{$r}{$s}/$gR_session{$s};
	    $nerr{$r}{$s}=$err{$r}{$s}/$ierr_session{$s};
	    $nerrk{$r}{$s}=$errk{$r}{$s}/$ierrk_session{$s};
	    $nq++;
	}
    }
    else{
	
	foreach my $s (sort {$a <=> $b} keys %{$ap{$r}}){
	    $t = $session_topic{$s};
	    next if ($ngrades_topic{$t}{1}+$ngrades_topic{$t}{2}+$ngrades_topic{$t}{3} == 0);  # skip if no rels?
	    $ap{$r}{$s}=$ap{$r}{$s}/($ngrades_topic{$t}{1}+$ngrades_topic{$t}{2}+$ngrades_topic{$t}{3});
	    $dcg{$r}{$s}=$dcg{$r}{$s}/$idcg{$t};
	    $dcgk{$r}{$s}=$dcgk{$r}{$s}/$idcgk{$t};
	    $gap{$r}{$s}=$gap{$r}{$s}/$gR{$t};
	    $nerr{$r}{$s}=$err{$r}{$s}/$ierr{$t};
	    $nerrk{$r}{$s}=$errk{$r}{$s}/$ierrk{$t};
	    $nq++;
	}
    }
}

# Include sessions without relevant documents
$nq = 76;

print "run\t\ttopic\tERR\tERR\@$k\tnERR\tnERR\@$k\tnDCG\tnDCG\@$k\tAP\tGAP\tPC\n";
my (%mndcg,%merr,%mndcgk,%merrk,%map,%mgap,%mnerr,%mnerrk, %mpc);
foreach my $r (sort {$a cmp $b} keys %ap)
{
    (my $run = $r) =~ s/($runprefix)//;
    
    foreach my $t (sort {$a <=> $b} keys %{$ap{$r}})
    {
	my $pp = sprintf("%s\t%s\t%.4f\t%.4f\t%.4f\t%.4f\t%.4f\t%.4f\t%.4f\t%.4f\t%.4f\n",$run,$t,$err{$r}{$t},$errk{$r}{$t},$nerr{$r}{$t},$nerrk{$r}{$t},$dcg{$r}{$t},$dcgk{$r}{$t},$ap{$r}{$t},$gap{$r}{$t},$pc{$r}{$t});
	print $pp if($alltopic == 1);

	# Computing the sum of the measures over all queries
	$mndcg{$r} += $dcg{$r}{$t};
	$merr{$r} += $err{$r}{$t};
	$mnerr{$r} += $nerr{$r}{$t};
	$mndcgk{$r} += $dcgk{$r}{$t};
	$merrk{$r} += $errk{$r}{$t};
	$mnerrk{$r} += $nerrk{$r}{$t};
	$map{$r} += $ap{$r}{$t};
	$mgap{$r} += $gap{$r}{$t};
	$mpc{$r} += $pc{$r}{$t};
    }
    # Computing the mean of the measures over all queries
    $mndcg{$r} = $mndcg{$r}/$nq;
    $merr{$r} = $merr{$r}/$nq;
    $mnerr{$r} = $mnerr{$r}/$nq;
    $mndcgk{$r} = $mndcgk{$r}/$nq;
    $merrk{$r} = $merrk{$r}/$nq;
    $mnerrk{$r} = $mnerrk{$r}/$nq;
    $map{$r} = $map{$r}/$nq;
    $mgap{$r} = $mgap{$r}/$nq;
    $mpc{$r} = $mpc{$r}/$nq;
    
    $pp =  sprintf("%s\tall\t%.4f\t%.4f\t%.4f\t%.4f\t%.4f\t%.4f\t%.4f\t%.4f\t%.4f\n",$run,$merr{$r},$merrk{$r},$mnerr{$r},$mnerrk{$r},$mndcg{$r},$mndcgk{$r},$map{$r},$mgap{$r},$mpc{$r});
    print $pp;
}
