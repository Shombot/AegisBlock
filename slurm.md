# Instructions on using slurm and environments

This will include some basic instructions on setting up your mamba/conda environment and using slurm to submit jobs. I will include basic slurm instructions that are needed and how to use them.
Also how to write a slurm script and what resources you might anticipate needing.

## Slurm Commands and Scripts

The way slurm works is when you submit a job it will run it based on available resources. If there aren't available resources you will be put in a queue. To run a job you want to first create a slurm script with specified gpu, memory, node, and cpu usage. Here is an example of a basic slurm script:

```bash
#!/bin/bash --login

#SBATCH --time=1-00:00:00   # walltime of 1 day
#SBATCH --ntasks-per-node=4 # number of processor cores (i.e. tasks)
#SBATCH --nodes=1   # number of nodes
#SBATCH --mem=128G   # memory per CPU core
#SBATCH --gres=gpu:4 # number of gpus
#SBATCH -J "name"   # job name
#SBATCH --output=%x_%j.out

# Set the max number of threads to use for programs using OpenMP. Should be <= ppn. Does nothing if the program doesn't use OpenMP.
export OMP_NUM_THREADS=$SLURM_CPUS_ON_NODE

# LOAD MODULES, INSERT CODE, AND RUN YOUR PROGRAMS HERE
nvidia-smi # shows your usage of gpus
mamba activate <environment_name>
python3 random.py
```
This is typically what running python code with a script will look like. Notice how you can specify gpus, memory, tasks per node, and wall-time. If your job goes beyond your wall-time it will automatically stop even if it is not finished.

To submit a job run:
```sbatch <script name>```

Once you submit a job it should output the job ID. You can check on your job ID with either ```scontrol show job <job_id>``` or ```squeue | grep <your_username>``` scontrol will print all the job info (quite a bit) whereas squeue will print out some information and it will print all your jobs you are running at once rather then just the specific ID if you grep it with your username. 

These commands will show if your job is RUNNING or PENDING, if it is pending there is a predicted start time of when the resources will be freed up and your job will start running.

To cancel a job run
```scancel <job_id>```

If your job is pending and you can tell what cluster it is trying to run on (usually it will show it) you can use ```squeue | grep <gpu/cluster_name>``` and it will show you what jobs are running or pending on that cluster, how many nodes/gpus the jobs are using, and how long they've been running for.

## Mamba/conda environment
### This is for using Python when you need to install packages
This is not related to slurm itself but when using slurm to submit jobs creating a mamba/conda environment is a good way to be able to download different packages 
without directly downloading them on the supercomputer (Bora). You can use module load within slurm but creating a mamba environment is a more viable solution since
you can download multiple modules and use them with just one line versus loading tons of modules.

Also when it comes to using mamba or conda within your terminal, you can use them interchangeably but mamba tends to be faster. This is what BYU says on the matter: 

We highly recommend using mamba instead of conda. mamba is a drop-in replacement for conda and does almost everything conda does, but faster. By also running mamba init, you will be able to use mamba.

### Mamba Initial Setup
First when setting it up you want to create a .bashrc file in your home directory containing this code which will setup conda (make sure it isn't already there)
```python
# >>> conda initialize >>>
# !! Contents within this block are managed by 'conda init' !!
__conda_setup="$('/apps/miniconda3/latest/bin/conda' 'shell.bash' 'hook' 2> /dev/null)"
if [ $? -eq 0 ]; then
    eval "$__conda_setup"
else
    if [ -f "/apps/miniconda3/latest/etc/profile.d/conda.sh" ]; then
        . "/apps/miniconda3/latest/etc/profile.d/conda.sh"
    else
        export PATH="/apps/miniconda3/latest/bin:$PATH"
    fi
fi
unset __conda_setup

if [ -f "/apps/miniconda3/latest/etc/profile.d/mamba.sh" ]; then
    . "/apps/miniconda3/latest/etc/profile.d/mamba.sh"
fi
# <<< conda initialize <<<
```

Then in some instances you may need to run this to finish setup, but once you do this it should automatically setup when you login to the supercomputer.

``` [[ -f ~/.bashrc ]] && source ~/.bashrc ```

Next you need to create a new mamba environment by running this in the terminal (put your own name in):

```linux
mamba create --name your_new_environment_name
```

Then you can activate the environment by running this command:

```mamba activate your_new_environment_name```

### Installing Packages
To install packages you can simply run:  

```mamba install <insert package name>```

But typically you want to check for different versions within the mamba environement and make sure they are compatible. Like when downloading python, download a version that will work with the packages you are going to be using. And when downloading new packages you can run a command that will list all the versions that can align with your python version. Here is an example for the tranformers package:

```mamba search pytorch```

This should list the specific versions, the versions will contain things like cpu or cuda. Cuda versions are compatible for using with gpu so make sure you know what you need/want. Typically mamba will automatically download the most recent version along with the cpu version. 

```
 pytorch 1.10.2  py3.6_cpu_0                  (+  34 builds) pytorch     linux-64
 pytorch 1.10.1  py3.6_cpu_0                  (+  30 builds) pytorch     linux-64
 pytorch 1.10.0  py3.6_cpu_0                  (+  45 builds) pytorch     linux-64
 pytorch 1.1.0   cuda100py27he554f03_0        (+   8 builds) pkgs        linux-64
 pytorch 1.0.1   cuda100py27he554f03_0        (+  11 builds) pkgs        linux-64
 pytorch 0.4.1   py27ha74772b_0               (+   3 builds) pkgs        linux-64
```

This is an example of some available pytorch specifications. The ones that say cuda are compatible with python 2.7, the ones above it are compatible with python 3.6.

### Adding Channels
Another thing for mamba setup is sometimes certain packages will be on the conda forge channel simply using ```mamba install <package_name>``` will not work. To make this issue easier you can add or 'pin' the conda forge channel and other channels. This is how you should do this (note if these commands don't work switch out mamba for conda)

```mamba config --add channels conda-forge```

This command then priortizes this conda-forge channel since it is more up to date:

```mamba config --set channel_priority strict```

You can also add other channels like pytorch.

### Basic Mamba Commands
To list the packages you have downloaded within your environment

```mamba list```

To deactivate your environment

```mamba deactivate```

To install a specific version of a package

```mamba install pytorch=2.5.1=py3.12_cuda12.4_cud-nn9.1.0_0```







