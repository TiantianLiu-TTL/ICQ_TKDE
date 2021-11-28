### Introduction
This project is for papaer "Contact Tracing over Uncertain Indoor Positioning Data".

This work studies a novel type of query called Indoor Contact Query (ICQ) over uncertain indoor positioning data that captures people's movements indoors. 

### How to run the code
1. Generate trajectory: run src/datagenerate/TrajectoryGen.java
2. Test Algorithm: run src/algorithm/Algo_ICQ_XXX.java
3. Conduct experiments:
  1) create a directory "result" under "ICQ_ICDE"
  2) run src/experiments/Test_XXX(parameter)
  3) run src/experiments/Acc_XXX(corrisponding parameter)
  4) result can be seen in directory "result". For each parameter, there will be four files: XX(dataset)_XX(parameter)_F.csv, XX(dataset)_XX(parameter)_recall.csv, XX(dataset)_XX(parameter)_time.csv, XX(dataset)_XX(parameter)_memory.csv
  
### Contact
1. [Tiantian Liu](https://tiantianliu-ttl.github.io/) (liutt@cs.aau.dk)
2. [Huan Li](http://people.cs.aau.dk/~lihuan/) (lihuan@cs.aau.dk)
3. [Hua Lu](https://luhua.ruc.dk/) (luhua@ruc.dk)
  
