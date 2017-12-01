

  naps-1.02-maxsat  is equivalent to naps-1.02 with -max-sat option


==== NaPS 1.x solver ==== 

Overall:
NaPS (Nagoya pseudo-Boolean solver) is a pseudo-Boolean solver for 
linear problems with Boolean variables developped at Nagoya University.

Linear constraint y1 - 3*y2 = 0 is described as
  1 y1 -3 y2 = 0 ;

NaPS follows MiniSAT+ for the input format, for example:

* #variable= 2 #constraint= 2
min: 1 x1 1 y1 ;
1 x1 -1 y1 >= 0 ;
1 x1  2 y1 <= 2 ;

where the first two lines are optional.


[Negated Boolean variable]
~x1 denotes the negation of x1 as the following example:
  1 ~x1 2 x2 3 ~x3 = 3;

[facility of definition]
In addition to ordinary constraints interpreted conjunction.  This
solver has a facility of definition, which enables us to write complex 
formulas like CNFs.  For example,
  d x3 => 1 x1 -2 x2 > 0 ;
represents a constraint meaning
  x3 implies x1 - 2*x2 >0

As another example, the following description denotes (c1 or c2) and
((not c2) or (not c3)):
  d x1 => c1 ;
  d x2 <=> c2 ;
  d x3 <= c3 ;
  1 x1 1 x2 >= 1 ;
  1 ~x2 1 ~x3 >= 1 ;
where in real description, c1, c2, and c3 should be replaced by
linear constraints.


[minimization]
Minimizing goal value facility is inherited from minisatp.
e.g.
  min: 1 x1 -1 x2

[model counting]
Finding number of models is possible, inherited from minisatp.
Projection model counting is also possible, where variables
that appear in minimization goal are focused on.  In this case,
minimization goal is ignored and used only for denoting a set of
variables.

[soft constraint]
Soft constraints are accepted, like
  [3] 1 y1 3 = 0
[3] shows the penalty in case of the model does not satisfy this.
The solver tries to find a model with minimum sum of penalties.

[dimacs inputs]
-dimacs option enables to read dimacs cnf files.

[maxsat mode]
-max-sat option states to solve maxsat cnf/wcnf.


===
NaPS 1.00 is a release, renamed version of gpw-2.18, 
where gpw solvers are developed based on minisatp-1.0: 
  http://minisat.se/MiniSat+.html
We thank Niklas Een and Niklas Srensson.

NaPS distribution contains several SAT-solvers.  NaPS invokes
Glueminisat by default.

All versions of gpw-2.x and NaPS-1.x are developped by Masahiko Sakai 
and his colleges.

----------------------------------------------------------------------
For showing its usage, try 
     naps --help
----------------------------------------------------------------------

See INSTALL for its installation.

Known BUG:
- -S may result wrong answer.
- Combination of -Simp and -es1 may result wrong answer.

Version 1.02 Apr 13, 2016
  - Modulo reduction for optimized-base [TACAS 2011]
    with primes up to 10000.
  - New cost function for optimized-base calculation (kind_digits).
  - A bug fixed on parsing
  - Parser routing for Dimacs format
  - MaxSAT competition mode
  - Several tunings (binary search, preserving memory in optimization, etc)
  - Added => and <= facility on definition.
  - Added the handler for SIGABRT commonly raised by lack of memory.
  - -Simp and -noSimp option introduced, which switches to
    'simplification solver' (-Simp is default if possible)
    -Simp works with glueminisat or ccminisat.
  - Fixed a bug on -model-check option.

Version 1.01 Mar 25, 2016.
  - Fixed the following bugs.
    = A bug when calling Simp-solvers in 1.00.
    = A bug when the simplification solver of glueminisat is used.
    = A bug on displaying 'o' value in rare case. 

Version 1.00  July 30, 2015.
  - released.  It was renamed from 'gpw version 2.18'.  In default,
    it uses simplification solver of glueminisat as an underlying
    SAT solver.

