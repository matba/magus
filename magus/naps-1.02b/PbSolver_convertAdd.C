/***************************************************************************[PbSolver_convertAdd.C]
Copyright (c) 2005-2010, Niklas Een, Niklas Sorensson

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
associated documentation files (the "Software"), to deal in the Software without restriction,
including without limitation the rights to use, copy, modify, merge, publish, distribute,
sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or
substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT
OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
**************************************************************************************************/

#include "PbSolver.h"
#include "Hardware.h"
#include "Debug.h"

#define lit2fml(p) id(var(var(p)),sign(p))


// Write 'd' in binary, then substitute 0 with '_0_', 1 with 'f'. This is the resulting 'out' vector.
//
static inline void bitAdder(Int d, Formula f, vec<Formula>& out)
{
    out.clear();
    for (; d != 0; d >>= 1)
        out.push(((d & 1) != 0) ? f : _0_);
}

// Produce a conjunction of formulas that forces the constraint 'xs <= ys'. The formulas will
// be pushed onto 'out'.
//
static Formula lte(vec<Formula>& xs, vec<Formula>& ys)
{
  //**/reportf("lte("); for(int i=0; i<xs.size(); i++){ dump(xs[i]); reportf(" "); } reportf(","); for(int i=0; i<ys.size(); i++){ dump(ys[i]); reportf(" "); } reportf(")\n");

    Formula f =_1_;

    for (int i = 0; i < xs.size(); i++){
        Formula c = _0_;
        for (int j = i+1; j < max(xs.size(), ys.size()); j++){
            Formula x = j < xs.size() ? xs[j] : _0_;
            Formula y = j < ys.size() ? ys[j] : _0_;
            c         = c | (x ^ y);
        }
        c = c | ~xs[i] | (i < ys.size() ? ys[i] : _0_);
        assert(c != _0_);
        if (c != _1_) {
	  f &= c;
	  //**/ reportf("and "); dump(c); reportf(" in i=%d \n", i);
	}
    }
  return f;
}

void linearAddition(const Linear& l, vec<Formula>& out)
{
    vec<Formula> sum;
    vec<Formula> inp;
    vec<Int>     cs;

    for (int i = 0; i < l.size; i++){
        inp.push(lit2fml(l[i]));
        //        inp.push(id(var(var(l[i])),sign(l[i])));
        cs.push(l(i));
    }

    Int     maxlim = (l.hi != Int_MAX) ? l.hi : (l.lo - 1);
    int     bits   = 0;
    for (Int i = maxlim; i != 0; i >>= 1)
        bits++;

    int     nodes = FEnv::nodes.size();

    //**/reportf("addOb(<"); for(int i=0; i<inp.size(); i++){ dump(inp[i]); reportf(","); } reportf(">,<"); for(int i=0; i<cs.size(); i++){reportf("%d,", toint(cs[i])); } reportf(">,sum,%d)\n", bits);

    addPb(inp,cs,sum,bits);
    stats_adder_cost = max(stats_adder_cost, FEnv::nodes.size() - nodes);
    stats_adder_constraints++;
    if (opt_verbosity >= 2){
        char* tmp = toString(maxlim);
        reportf("Adder-cost: %d   maxlim: %s   bits: %d/%d\n", FEnv::nodes.size() - nodes, tmp, sum.size(), bits);
        xfree(tmp); }

    Formula f= _1_ ;
    if (l.lo > 0){
      //reportf("lower limit\n");
      bitAdder(l.lo,_1_,inp);
      f &= lte(inp,sum);
      if(l.hi == Int_MAX)
	stats_std_form++;
    }
    if (l.hi != Int_MAX){
      //reportf("upper limit\n");
      bitAdder(l.hi,_1_,inp);
      f &= lte(sum,inp);
      stats_band_form++;
    }
    if(l.llt==lit_Undef) {
      out.push( f );
    } else {
      // f ^= ~d;  // f = f exor d
      if(l.llt!=lit_Undef)
	out.push( ~lit2fml(l.llt) | f );  // llt => f
/*
      if(l.rlt!=lit_Undef)
	out.push( lit2fml(l.rlt) | ~f );  // rlt <= f
*/
    }
    //**/ reportf("out.size()=%d\n",out.size());
}


