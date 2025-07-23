#!/bin/bash

#SBATCH --partition=gpu-l40
#SBATCH --time=1-00:00:00   # walltime 24hr
#SBATCH --ntasks-per-node=4 # number of processor cores (i.e. tasks)
#SBATCH --nodes=2   # number of nodes
#SBATCH --mem=128G   # memory per CPU core
#SBATCH --gres=gpu:0 # number of GPUs per node
#SBATCH -J "test_script"   # job name
#SBATCH --output=%x_%j.out

# Set the max number of threads to use for programs using OpenMP. Should be <= ppn. Does nothing if the program doesn't use OpenMP.
export OMP_NUM_THREADS=$SLURM_CPUS_ON_NODE

# LOAD MODULES, INSERT CODE, AND RUN YOUR PROGRAMS HERE
module load openjdk/24.0.1
java -jar e3_50.jar # input code you want to compile
