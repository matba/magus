/****************************************************************************************[Solver.h]
Copyright (c) 2003-2006, Niklas Een, Niklas Sorensson
Copyright (c) 2007-2010, Niklas Sorensson

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

#ifndef GlueMiniSat_Solver_h
#define GlueMiniSat_Solver_h

#include "mtl/Vec.h"
#include "mtl/Heap.h"
#include "mtl/Alg.h"
#include "utils/Options.h"
#include "core/SolverTypes.h"

// added by nabesima
#include "RecentVals.h"
#define GLUEGlueMiniSat_VERSION "glueminisat2.2.6-ucore-g"


namespace GlueMiniSat {

#define DRV_NOT_DELETED      0
#define DRV_CLAUSE_DELETED   1
#define DRV_DELETED          2

//=================================================================================================
// Solver -- the main class:

class Solver {
public:

    // Constructor/Destructor:
    //
    Solver();
    virtual ~Solver();

    void    setMiniSat22Params();
    void    setGlucose10Params();
    void    setUcoreExtract(int n);

    // Problem specification:
    //
    Var     newVar    (bool polarity = true, bool dvar = true); // Add a new variable with parameters specifying variable mode.

    bool    addClause (const vec<Lit>& ps);                     // Add a clause to the solver.
    bool    addEmptyClause();                                   // Add the empty clause, making the solver contradictory.
    bool    addClause (Lit p);                                  // Add a unit clause to the solver.
    bool    addClause (Lit p, Lit q);                           // Add a binary clause to the solver.
    bool    addClause (Lit p, Lit q, Lit r);                    // Add a ternary clause to the solver.
    bool    addClause_(      vec<Lit>& ps);                     // Add a clause to the solver without making superflous internal copy. Will
                                                                // change the passed vector 'ps'.

    // Solving:
    //
    bool    simplify     ();                        // Removes already satisfied clauses.
    bool    solve        (const vec<Lit>& assumps); // Search for a model that respects a given set of assumptions.
    lbool   solveLimited (const vec<Lit>& assumps); // Search for a model that respects a given set of assumptions (With resource constraints).
    bool    solve        ();                        // Search without assumptions.
    bool    solve        (Lit p);                   // Search for a model that respects a single assumption.
    bool    solve        (Lit p, Lit q);            // Search for a model that respects two assumptions.
    bool    solve        (Lit p, Lit q, Lit r);     // Search for a model that respects three assumptions.
    bool    okay         () const;                  // FALSE means solver is in a conflicting state

    void    toDimacs     (FILE* f, const vec<Lit>& assumps);            // Write CNF to file in DIMACS-format.
    void    toDimacs     (const char *file, const vec<Lit>& assumps);
    void    toDimacs     (FILE* f, Clause& c, vec<Var>& map, Var& max);

    // Convenience versions of 'toDimacs()':
    void    toDimacs     (const char* file);
    void    toDimacs     (const char* file, Lit p);
    void    toDimacs     (const char* file, Lit p, Lit q);
    void    toDimacs     (const char* file, Lit p, Lit q, Lit r);

    // Variable mode:
    //
    void    setPolarity    (Var v, bool b); // Declare which polarity the decision heuristic should use for a variable. Requires mode 'polarity_user'.
    void    setDecisionVar (Var v, bool b); // Declare if a variable should be eligible for selection in the decision heuristic.

    // Read state:
    //
    lbool   value      (Var x) const;       // The current value of a variable.
    lbool   value      (Lit p) const;       // The current value of a literal.
    lbool   modelValue (Var x) const;       // The value of a variable in the last model. The last call to solve must have been satisfiable.
    lbool   modelValue (Lit p) const;       // The value of a literal in the last model. The last call to solve must have been satisfiable.
    int     nAssigns   ()      const;       // The current number of assigned literals.
    int     nClauses   ()      const;       // The current number of original clauses.
    int     nLearnts   ()      const;       // The current number of learnt clauses.
    int     nVars      ()      const;       // The current number of variables.
    int     nFreeVars  ()      const;

    // Resource contraints:
    //
    void    setConfBudget(int64_t x);
    void    setPropBudget(int64_t x);
    void    budgetOff();
    void    interrupt();          // Trigger a (potentially asynchronous) interruption of the solver.
    void    clearInterrupt();     // Clear interrupt indicator flag.

    // Memory managment:
    //
    virtual void garbageCollect();
    void    checkGarbage(double gf);
    void    checkGarbage();

    // Added by nabesima
    bool     extractUCore();
    uint32_t lastClauseID() { return clause_id; }

    // added by nabesima for gpw's interface.
    bool&    getOK            ()              { return ok;           }
    lbool    getAssign        (Var v  ) const { return assigns[v];   }
    Lit      getTrail         (int idx) const { return trail[idx];   }
    int      getTrailSize     ()        const { return trail.size(); }
    // added by sakai for gpw's interface.
    int      getUcore_vars        (int idx) const { return ucore_vars[idx];   }
    int      getUcore_varsSize    ()        const { return ucore_vars.size(); }
    int      getUcore_clauses     (int idx) const { return ucore_clauses[idx];   }
    int      getUcore_clausesSize ()        const { return ucore_clauses.size(); }

    // Extra results: (read-only member variable)
    //
    vec<lbool> model;             // If problem is satisfiable, this vector contains the model (if any).
    vec<Lit>   conflict;          // If problem is unsatisfiable (possibly under assumptions),
                                  // this vector represent the final conflict clause expressed in the assumptions.
    vec<uint32_t> ucore_clauses;  // added by daiki
    vec<uint32_t> ucore_vars;     // added by daiki

    // Mode of operation:
    //
    int       verbosity;
    double    var_decay;
    double    clause_decay;
    double    random_var_freq;
    double    random_seed;
    bool      luby_restart;
    int       ccmin_mode;         // Controls conflict clause minimization (0=none, 1=basic, 2=deep).
    int       phase_saving;       // Controls the level of phase saving (0=none, 1=limited, 2=full).
    bool      rnd_pol;            // Use random polarities for branching heuristics.
    bool      rnd_init_act;       // Initialize variable activities with a small random value.
    double    garbage_frac;       // The fraction of wasted memory allowed before a garbage collection is triggered.

    int       restart_first;      // The initial restart limit.                                                                (default 100)
    double    restart_inc;        // The factor with which the restart limit is multiplied in each restart.                    (default 1.5)
    double    learntsize_factor;  // The intitial limit for learnt clauses is a factor of the original clauses.                (default 1 / 3)
    double    learntsize_inc;     // The limit for learnt clauses is multiplied with this factor each restart.                 (default 1.1)

    int       learntsize_adjust_start_confl;
    double    learntsize_adjust_inc;

    // add by daiki
    int       extract_ucore;      // Extracts an unsat core.
    bool      verify_ucore;       // Verifies a found unsat core.
    bool      save_freq;          // Save the number of picks and assigns for each variable.

    // Statistics: (read-only member variable)
    //
    uint64_t solves, starts, decisions, rnd_decisions, propagations, conflicts;
    uint64_t dec_vars, clauses_literals, learnts_literals, max_literals, tot_literals;

    // added by nabesima
    uint32_t      init_vars, simp_vars, init_clauses, simp_clauses;
    uint64_t      tot_lbds, curr_restarts;
    uint64_t      same_restarts;
    uint64_t      max_confs, min_confs;
    uint32_t      reduce_dbs;              // The number of calls for reduce database
    uint64_t      removed_decisions;       // The number of removed decisions by light-weight restart
    uint64_t      removed_propagations;    // The number of removed propagations by light-weight restart
    uint32_t      glue_clauses;            // The number of glue clauses.
    vec<uint32_t> num_picks;               // The number of picks for each variable.
    vec<uint32_t> num_assigns;             // The number of assigns for each variable.
    uint32_t      removed_derivations;     // The number of removed derivations.
    int           merged_clause_ids;       // The number of merged clause ids.
    double        ucore_extraction_time;   // The time of unsat core extraction after solving.

protected:

    // Helper structures:
    //
    struct VarData { CRef reason; int level; };
    static inline VarData mkVarData(CRef cr, int l){ VarData d = {cr, l}; return d; }

    struct Watcher {
        CRef cref;
        Lit  blocker;
        Watcher(CRef cr, Lit p) : cref(cr), blocker(p) {}
        bool operator==(const Watcher& w) const { return cref == w.cref; }
        bool operator!=(const Watcher& w) const { return cref != w.cref; }
    };

    struct WatcherDeleted
    {
        const ClauseAllocator& ca;
        WatcherDeleted(const ClauseAllocator& _ca) : ca(_ca) {}
        bool operator()(const Watcher& w) const { return ca[w.cref].mark() == 1; }
    };

    struct VarOrderLt {
        const vec<double>&  activity;
        const vec<uint32_t>&    var_freq;    // added by nabesima
        bool operator () (Var x, Var y) const {
            if (var_freq.size() == 0)
                return activity[x] > activity[y];
            if (activity[x] != activity[y])
                return activity[x] > activity[y];
            return  var_freq[x] > var_freq[y];
        }
        VarOrderLt(const vec<double>&  act, const vec<uint32_t>& freq) : activity(act), var_freq(freq) { }
    };

    // added by nabesima
    struct Derivation {
        uint32_t cid;  		        // Clause ID
        struct {
            unsigned learnt      : 1;
            unsigned deleted     : 2;
            unsigned checked     : 1;
            unsigned child_count : 28;       // Reference count by childrens
        } header;
        vec<uint32_t> parents;	 // Parent clause IDs
        Derivation(bool learnt=true) : cid(0) {
            header.learnt      = learnt;
            header.deleted     = DRV_NOT_DELETED;
            header.checked     = 0;
            header.child_count = 0;
        }
        Derivation&  operator = (Derivation& other) {
            if (this == &other)
                return *this;
            cid         = other.cid;
            header      = other.header;
            other.parents.moveTo(parents);
            return *this;
        }

        bool     learnt ()              { return header.learnt; }
        void     learnt (bool lrnt)     { header.learnt = lrnt; }
        bool     checked()              { return header.checked; }
        void     check  ()              { header.checked = true; }
        uint32_t deleted()              { return header.deleted; }
        void     deleted(uint32_t type) { assert(DRV_NOT_DELETED <= type && type <= DRV_DELETED); header.deleted = type; }
        int      childCount()           { return header.child_count; }
        void     clearChildCount()      { header.child_count = 0; }
        void     incChildCound()        { header.child_count++; assert(header.child_count > 0); }
        void     decChildCound()        { assert(header.child_count > 0); header.child_count--;  }
    };

    // Solver state:
    //
    bool                ok;               // If FALSE, the constraints are already unsatisfiable. No part of the solver state may be used!
    vec<CRef>           clauses;          // List of problem clauses.
    vec<CRef>           learnts;          // List of learnt clauses.
    double              cla_inc;          // Amount to bump next clause with.
    vec<double>         activity;         // A heuristic measurement of the activity of a variable.
    double              var_inc;          // Amount to bump next variable with.
    OccLists<Lit, vec<Watcher>, WatcherDeleted>
                        watches;          // 'watches[lit]' is a list of constraints watching 'lit' (will go there if literal becomes true).
    vec<lbool>          assigns;          // The current assignments.
    vec<char>           polarity;         // The preferred polarity of each variable.
    vec<char>           decision;         // Declares if a variable is eligible for selection in the decision heuristic.
    vec<Lit>            trail;            // Assignment stack; stores all assigments made in the order they were made.
    vec<int>            trail_lim;        // Separator indices for different decision levels in 'trail'.
    vec<VarData>        vardata;          // Stores reason and level for each variable.
    int                 qhead;            // Head of queue (as index into the trail -- no more explicit propagation queue in MiniSat).
    int                 simpDB_assigns;   // Number of top-level assignments since last execution of 'simplify()'.
    int64_t             simpDB_props;     // Remaining number of propagations that must be made before next execution of 'simplify()'.
    vec<Lit>            assumptions;      // Current set of assumptions provided to solve by the user.
    Heap<VarOrderLt>    order_heap;       // A priority queue of variables ordered with respect to the variable activity.
    double              progress_estimate;// Set by 'search()'.
    bool                remove_satisfied; // Indicates whether possibly inefficient linear scan for satisfied clauses should be performed in 'simplify'.

    ClauseAllocator     ca;

    // Temporaries (to reduce allocation overhead). Each variable is prefixed by the method in which it is
    // used, exept 'seen' wich is used in several places.
    //
    vec<char>           seen;
    vec<Lit>            analyze_stack;
    vec<Lit>            analyze_toclear;
    // add by daiki
    vec<Lit>            analyze_fixed_toclear;

    vec<Lit>            add_tmp;

    double              max_learnts;
    double              learntsize_adjust_confl;
    int                 learntsize_adjust_cnt;

    // Resource contraints:
    //
    int64_t             conflict_budget;    // -1 means no budget.
    int64_t             propagation_budget; // -1 means no budget.
    bool                asynch_interrupt;

    // added by nabesima
    int 	  learnts_measure;    // The measure to predict leanrt quality.
    int       reduce_db;          // The measure for reducing learnts.
    bool	  ag_reduce_db;       // Use the aggressive reduce DB strategy.
    int 	  max_lbd;			  // The max LBD of survived learnt clauses in reduce DB.
    int 	  max_len;			  // The max length of survived learnt clauses in reduce DB.
    int       restart_strategy;   // The restart strategy.
    int       lw_restart;         // Use the light-weight restart
    int       drastic_restart;    // Use the drastic restart
    double    lbd_restart_rate;   // Restarts if local LBD average * this val < global one.
    double    dlv_restart_rate;   // Restarts if local DLV average * this val < global one.
    double    conf_restart_rate;  // Restarts if local CNF average * this val < global one.
    int       restart_min_confs;  // The number of conflict for next restart.
    int       restart_stricting;  // If > 0, then restart conditions are gradually strincting.
    int       var_decay_strategy; // The var-decay parameter strategy.
    int       var_decay_period;   // The parameter for the var-decay strategy.
    double    init_var_decay;     // The initial var-decay parameter in incremenetal var-decay strategy.
    double    max_var_decay;      // The maximum var-decay parameter in incremenetal var-decay strategy.
    bool      var_freq_ordering;  // Use variable frequency as 2nd preference for decision
    vec<uint32_t> var_freq;       // The number of occurences for each variable.
    int       cir;                // Use counter implication restart
    vec<int>  num_implied;        // The counter of implied variables frequency
    int       lbd_act_bumping;    // The LBD based activity strategy.
    vec<vec<Lit> >
              fronzen_clauses;   // The remained clauses in relaxation approach.

    //add by daiki
    uint32_t         clause_id;            // Clause Identifier.
    vec<uint32_t>    line2cid;             // A mapping from line no to clause id.
    vec<uint32_t>    used_clauses;         // Used clauses to derive a learnt clause.
    vec<uint32_t>    temp_used_clauses;    // Temporary clauses to minimize a learnt clause.
    uint32_t         empty_reason;         // Reasons of the derivation of an empty clause.
    vec<uint32_t>    fixed_reason;         // Reasons of unit propagated literals at DLV = 0.
    vec<Derivation>  derivations;          // List of derivations.

    bool      init_rdb_param;     // Initialize if true (for incremental SAT solving)
    int       reduce_db_base;     // The initial reduce-DB limit.
    int       reduce_db_inc;      // The factor with which the reduce-DB limit is added to the limit.
    double    min_rate_learnts;   // The min rate of learnts to be preserved at reduction.
    double    max_rate_learnts;   // The max rate of learnts to be preserved at reduction.
    uint64_t  reduce_db_limit;    // The current limit for reducing DB.

    vec<Lit>  implied_by_learnts; // The literals implied by learnts.
    uint32_t  lbd_updates;        // The number of the LBD computation.
    vec<uint32_t> lbd_time;   	  // The counter to count the LBD for a learnt.

    RecentVals<> recentLBDs;      // The bounded queue for storing recent LBDs.
    RecentVals<> recentDLVs;      // The bounded queue for storing recent decision levels.
    double     wholeLBDs; 	      // The whole sum of LBDs.
    double     wholeDLVs;         // The whole sum of conflicting decision levels.

    // Main internal methods:
    //
    void     insertVarOrder   (Var x);                                                 // Insert a variable in the decision order priority queue.
    Lit      pickBranchLit    ();                                                      // Return the next decision variable.
    void     newDecisionLevel ();                                                      // Begins a new decision level.
    void     uncheckedEnqueue (Lit p, CRef from = CRef_Undef);                         // Enqueue a literal. Assumes value of literal is undefined.
    bool     enqueue          (Lit p, CRef from = CRef_Undef);                         // Test if fact 'p' contradicts current state, enqueue otherwise.
    CRef     propagate        ();                                                      // Perform unit propagation. Returns possibly conflicting clause.
    void     cancelUntil      (int level);                                             // Backtrack until a certain level.
    // added by nabesima
    int      permutatedTrail  ();                                                      // Return the next restart level.
    // modified by nabesima
    //void     analyze          (CRef confl, vec<Lit>& out_learnt, int& out_btlevel);    // (bt = backtrack)
    void     analyze          (CRef confl, vec<Lit>& out_learnt, int& out_btlevel, int& lbd);    // (bt = backtrack)
    void     analyzeFinal     (Lit p, vec<Lit>& out_conflict);                         // COULD THIS BE IMPLEMENTED BY THE ORDINARIY "analyze" BY SOME REASONABLE GENERALIZATION?
    bool     litRedundant     (Lit p, uint32_t abstract_levels);                       // (helper method for 'analyze()')
    lbool    search           (int nof_conflicts);                                     // Search for a given number of conflicts.
    lbool    solve_           ();                                                      // Main solve method (assumptions given in 'assumptions').
    void     reduceDB         ();                                                      // Reduce the set of learnt clauses.
    void     removeSatisfied  (vec<CRef>& cs);                                         // Shrink 'cs' to contain only non-satisfied clauses.
    void     rebuildOrderHeap ();

    // Maintaining Variable/Clause activity:
    //
    // modified by nabesima
    //void     varDecayActivity ();                    // Decay all variables with the specified factor. Implemented by increasing the 'bump' value instead.
    void     varDecayActivity (bool light = true);     // Decay all variables with the specified factor. Implemented by increasing the 'bump' value instead.
    void     varBumpActivity  (Var v, double inc);     // Increase a variable with the current 'bump' value.
    void     varBumpActivity  (Var v);                 // Increase a variable with the current 'bump' value.
    void     claDecayActivity ();                      // Decay all clauses with the specified factor. Implemented by increasing the 'bump' value instead.
    void     claBumpActivity  (Clause& c);             // Increase a clause with the current 'bump' value.

    // Operations on clauses:
    //
    void     attachClause     (CRef cr);               // Attach a clause to watcher lists.
    void     detachClause     (CRef cr, bool strict = false); // Detach a clause to watcher lists.
    void     detachAllClauses ();                      // Detach all clauess (added by nabesima)
    void     removeClause     (CRef cr);               // Detach and free a clause.
    void     removeClauseNoDetach(CRef cr);            // free a clause without detach (added by nabesima)
    bool     locked           (const Clause& c) const; // Returns TRUE if a clause is a reason for some implication in the current state.
    bool     satisfied        (const Clause& c) const; // Returns TRUE if a clause is satisfied in the current state.

    void     relocAll         (ClauseAllocator& to);

    // Misc:
    //
    int      decisionLevel    ()      const; // Gives the current decisionlevel.
    uint32_t abstractLevel    (Var x) const; // Used to represent an abstraction of sets of decision levels.
    CRef     reason           (Var x) const;
    int      level            (Var x) const;
    double   progressEstimate ()      const; // DELETE THIS ?? IT'S NOT VERY USEFUL ...
    bool     withinBudget     ()      const;

    //add by daiki
    void     addClauseToDrvTree(uint32_t cid, vec<uint32_t>& used);
    void     addDerivation(uint32_t cid, vec<Lit>& lits);
    void     addLearntToDrvTree(uint32_t cid, vec<uint32_t>& used);
    void     addClauseToDerivation(uint32_t cid, uint32_t used);
    uint32_t binarySearch(vec<Derivation>& list, uint32_t key);
    uint32_t binarySearch(vec<uint32_t>& list, uint32_t key);
    uint32_t linearSearch(vec<Derivation>& list, uint32_t key);
    uint32_t linearSearch(vec<uint32_t>& list, uint32_t key);
    CRef     findClause(vec<CRef> &cs, uint32_t cid);
    void     traceCIG(uint32_t learnt);
    void     removeRedundantDrerivations();
    void     updateChildCounts();
    void     checkChildCount();
    bool     verifyUCore();

    // Static helpers:
    //

    // Returns a random float 0 <= x < 1. Seed must never be 0.
    static inline double drand(double& seed) {
        seed *= 1389796;
        int q = (int)(seed / 2147483647);
        seed -= (double)q * 2147483647;
        return seed / 2147483647; }

    // Returns a random integer 0 <= x < size. Seed must never be 0.
    static inline int irand(double& seed, int size) {
        return (int)(drand(seed) * size); }

    // added by nabesima
    void printLit(Lit l);
    void printLitWithModel(Lit l);
    void printLits(vec<Lit>& lits);
    void printSortedLits(vec<Lit>& lits);
    void printLitsWithModel(vec<Lit>& lits);
    void printClause(Clause& c);
    void printSortedClause(Clause& c);
    void printClauseWithModel(Clause& c);
    void printDecisionStack(int from);
    void printLog();

    // CIR by Sonobe
    void counterImplicationRestart() {
        if (cir == 0) return;
        if (starts % cir != 0) return;

        int max_imp = 0;
        for (int i = trail.size() - 1; i >= 0; i--){
            int v  = var(trail[i]);
            int lv = level(v);
            CRef r = reason(v);
            if(r == CRef_Undef)
                continue;
            Clause& c = ca[r];
            for(int j = 0; j < c.size(); j++){
                int w = var(c[j]);
                if(level(w) == lv)
                    continue;
                num_implied[v]++;
            }
            if(max_imp < num_implied[v])
                max_imp = num_implied[v];
        }
        for(int c = trail.size() - 1; c >= 0; c--){
            int v = var(trail[c]);
            if(num_implied[v] == 0)
                continue;
            varBumpActivity(v, var_inc * num_implied[v] * 9973 / max_imp);
            num_implied[v] = 0;
        }
    }
};

//=================================================================================================
// Implementation of inline methods:

inline CRef Solver::reason(Var x) const { return vardata[x].reason; }
inline int  Solver::level (Var x) const { return vardata[x].level; }

inline void Solver::insertVarOrder(Var x) {
    if (!order_heap.inHeap(x) && decision[x]) order_heap.insert(x); }

// modified by nabesima
inline void Solver::varDecayActivity(bool light) {
    if (light)
        var_inc *= (1 / var_decay);
    else
        for (int i = 0; i < nVars(); i++)
            activity[i] *= 1e-100;
}
inline void Solver::varBumpActivity(Var v) { varBumpActivity(v, var_inc); }
inline void Solver::varBumpActivity(Var v, double inc) {
    if ( (activity[v] += inc) > 1e100 ) {
        // Rescale:
        for (int i = 0; i < nVars(); i++)
            activity[i] *= 1e-100;
        var_inc *= 1e-100; }

    // Update order_heap with respect to new activity:
    if (order_heap.inHeap(v))
        order_heap.decrease(v);
}

inline void Solver::claDecayActivity() { cla_inc *= (1 / clause_decay); }
inline void Solver::claBumpActivity (Clause& c) {
    if ( (c.activity() += cla_inc) > 1e20 ) {
        // Rescale:
        for (int i = 0; i < learnts.size(); i++)
            ca[learnts[i]].activity() *= 1e-20;
        cla_inc *= 1e-20; } }
inline void Solver::checkGarbage(void){ return checkGarbage(garbage_frac); }
inline void Solver::checkGarbage(double gf){
    if (ca.wasted() > ca.size() * gf)
        garbageCollect(); }

// NOTE: enqueue does not set the ok flag! (only public methods do)
inline bool     Solver::enqueue         (Lit p, CRef from)      { return value(p) != l_Undef ? value(p) != l_False : (uncheckedEnqueue(p, from), true); }
inline bool     Solver::addClause       (const vec<Lit>& ps)    { ps.copyTo(add_tmp); return addClause_(add_tmp); }
inline bool     Solver::addEmptyClause  ()                      { add_tmp.clear(); return addClause_(add_tmp); }
inline bool     Solver::addClause       (Lit p)                 { add_tmp.clear(); add_tmp.push(p); return addClause_(add_tmp); }
inline bool     Solver::addClause       (Lit p, Lit q)          { add_tmp.clear(); add_tmp.push(p); add_tmp.push(q); return addClause_(add_tmp); }
inline bool     Solver::addClause       (Lit p, Lit q, Lit r)   { add_tmp.clear(); add_tmp.push(p); add_tmp.push(q); add_tmp.push(r); return addClause_(add_tmp); }
inline bool     Solver::locked          (const Clause& c) const { return value(c[0]) == l_True && reason(var(c[0])) != CRef_Undef && ca.lea(reason(var(c[0]))) == &c; }
inline void     Solver::newDecisionLevel()                      { trail_lim.push(trail.size()); }

inline int      Solver::decisionLevel ()      const   { return trail_lim.size(); }
inline uint32_t Solver::abstractLevel (Var x) const   { return 1 << (level(x) & 31); }
inline lbool    Solver::value         (Var x) const   { return assigns[x]; }
inline lbool    Solver::value         (Lit p) const   { return assigns[var(p)] ^ sign(p); }
inline lbool    Solver::modelValue    (Var x) const   { return model[x]; }
inline lbool    Solver::modelValue    (Lit p) const   { return model[var(p)] ^ sign(p); }
inline int      Solver::nAssigns      ()      const   { return trail.size(); }
inline int      Solver::nClauses      ()      const   { return clauses.size(); }
inline int      Solver::nLearnts      ()      const   { return learnts.size(); }
inline int      Solver::nVars         ()      const   { return vardata.size(); }
inline int      Solver::nFreeVars     ()      const   { return (int)dec_vars - (trail_lim.size() == 0 ? trail.size() : trail_lim[0]); }
inline void     Solver::setPolarity   (Var v, bool b) { polarity[v] = b; }
inline void     Solver::setDecisionVar(Var v, bool b)
{
    if      ( b && !decision[v]) dec_vars++;
    else if (!b &&  decision[v]) dec_vars--;

    decision[v] = b;
    insertVarOrder(v);
}
inline void     Solver::setConfBudget(int64_t x){ conflict_budget    = conflicts    + x; }
inline void     Solver::setPropBudget(int64_t x){ propagation_budget = propagations + x; }
inline void     Solver::interrupt(){ asynch_interrupt = true; }
inline void     Solver::clearInterrupt(){ asynch_interrupt = false; }
inline void     Solver::budgetOff(){ conflict_budget = propagation_budget = -1; }
inline bool     Solver::withinBudget() const {
    return !asynch_interrupt &&
           (conflict_budget    < 0 || conflicts < (uint64_t)conflict_budget) &&
           (propagation_budget < 0 || propagations < (uint64_t)propagation_budget); }

// FIXME: after the introduction of asynchronous interrruptions the solve-versions that return a
// pure bool do not give a safe interface. Either interrupts must be possible to turn off here, or
// all calls to solve must return an 'lbool'. I'm not yet sure which I prefer.
inline bool     Solver::solve         ()                    { budgetOff(); assumptions.clear(); return solve_() == l_True; }
inline bool     Solver::solve         (Lit p)               { budgetOff(); assumptions.clear(); assumptions.push(p); return solve_() == l_True; }
inline bool     Solver::solve         (Lit p, Lit q)        { budgetOff(); assumptions.clear(); assumptions.push(p); assumptions.push(q); return solve_() == l_True; }
inline bool     Solver::solve         (Lit p, Lit q, Lit r) { budgetOff(); assumptions.clear(); assumptions.push(p); assumptions.push(q); assumptions.push(r); return solve_() == l_True; }
inline bool     Solver::solve         (const vec<Lit>& assumps){ budgetOff(); assumps.copyTo(assumptions); return solve_() == l_True; }
inline lbool    Solver::solveLimited  (const vec<Lit>& assumps){ assumps.copyTo(assumptions); return solve_(); }
inline bool     Solver::okay          ()      const   { return ok; }

inline void     Solver::toDimacs     (const char* file){ vec<Lit> as; toDimacs(file, as); }
inline void     Solver::toDimacs     (const char* file, Lit p){ vec<Lit> as; as.push(p); toDimacs(file, as); }
inline void     Solver::toDimacs     (const char* file, Lit p, Lit q){ vec<Lit> as; as.push(p); as.push(q); toDimacs(file, as); }
inline void     Solver::toDimacs     (const char* file, Lit p, Lit q, Lit r){ vec<Lit> as; as.push(p); as.push(q); as.push(r); toDimacs(file, as); }


//=================================================================================================
// Debug etc:


//=================================================================================================
}

#endif
