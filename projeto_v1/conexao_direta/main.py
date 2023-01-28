import time
from ast import Try
import time
from modulo_comunicacao import Conexao
import modulo_comunicacao as c

time_exec_carga = []
time_exec_mysql = []
time_exec_mongo = []


def realizar_consulta():
    id = 1
    mysql=0
    mongodb=0
    fim_exec_mysql=0
    inicio_exec_mysql=0
    fim_exec_mongoDB=0
    inicio_exec_mongoDB=0
    
    c = Conexao()
    
    with open('consultas_D_c1.txt', 'r') as consultasNL:
        consultas = consultasNL.readlines()

    for consulta in consultas:
        # tipo 1 SQL e 2 NoSQL
        if(id == 1):
            try:
                time.sleep(3)
                print(consulta)
                inicio_exec_mysql = time.time()
                print(c.realiza(id, consulta), "\n")
                fim_exec_mysql = time.time()
                time_exec_mysql.append(fim_exec_mysql-inicio_exec_mysql)
                id = 2
            except:
                print("Erro, não foi possivel obter a resposta pelo BD SQL\n")
                id = 2
        elif(id == 2):
            try:
                time.sleep(3)
                print(consulta)
                inicio_exec_mongoDB = time.time()
                print(c.realiza(id, consulta), "\n")
                fim_exec_mongoDB = time.time()
                time_exec_mongo.append(fim_exec_mongoDB - inicio_exec_mongoDB)
                id = 1
            except:
                print("Erro, não foi possivel obter a resposta pelo BD NoSQL\n")
                id = 1

def main():
    i=1
    while(i<3):
        print("repeticao inicio: ", i)
        inicio_exec = time.time()  # Inicio do tempo de execução completo da carga
        realizar_consulta()
        fim_exec = time.time()  # Fim do tempo de execução completo da carga
        time_exec_carga.append(fim_exec-inicio_exec) # Vetor com os tempos totais de execucao de carga
    
        print("Tempos de resposta da carga:")
        print(time_exec_carga)

        print("Tempos de resposta do banco mysql:")
        print(sum(time_exec_mysql))

        print("Tempos de resposta do banco mongodb:")
        print(sum(time_exec_mongo))

        i=i+1
        
    time_exec_carga.clear()
    time_exec_mysql.clear()
    time_exec_mongo.clear()
    
if __name__ == "__main__":
    main()
