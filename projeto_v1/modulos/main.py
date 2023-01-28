from ast import Try
import time
from modulo_interface import Mensagem
from modulo_traducao import Traduz
from modulo_comunicacao import Conexao
import modulo_comunicacao as c

time_exec_carga = []
time_trad_carga = []
time_exec_mysql = []
time_exec_mongo = []

def realizar_consulta():
    c = Conexao()
    t = Traduz()
    total=0
    
    with open('consultasNL_c1.txt', 'r') as consultasNL:
        consultas = consultasNL.readlines()

    for consulta in consultas:
        mensagem = Mensagem(consulta[0:-2])

        inicio_trad = time.time()  # Inicio do tempo de traducao da consulta
        mensagem.set_csql(t.ln2Sql(mensagem.get_consulta_nl()))
        mensagem.set_cnosql(t.sqlToMongo(mensagem.get_csql()))
        fim_trad = time.time()  # Fim do tempo de traducao da consulta
        print(mensagem.__str__())    

        inicio_exec_mysql = time.time()
        print(c.realiza(1, mensagem.get_csql()), "\n")
        fim_exec_mysql = time.time()
        time.sleep(3)
        
        inicio_exec_mongodb = time.time()
        print(c.realiza(2, mensagem.get_cnosql()), "\n")
        fim_exec_mongodb = time.time()
        time.sleep(3)
        
        time_exec_mysql.append(fim_exec_mysql - inicio_exec_mysql)
        time_exec_mongo.append(fim_exec_mongodb - inicio_exec_mongodb)
        
        total=total+(fim_trad-inicio_trad) #Soma total do tempo de todas as traducoes da carga
    time_trad_carga.append(total) # Vetor com os tempos totais de traducao de carga
    total = 0

def main():
    inicio_exec_carga = time.time()  # Inicio do tempo de execução completo da carga
    realizar_consulta()
    fim_exec_carga = time.time()  # Fim do tempo de execução completo da carga
    time_exec_carga.append(fim_exec_carga-inicio_exec_carga) # Vetor com os tempos totais de execucao de carga
    
    print("Tempos de traduçao da carga:")
    print(time_trad_carga)
    
    print("Tempos de execucao da carga:")
    print(time_exec_carga)
    
    print("Tempos de resposta do mysql:")
    print(sum(time_exec_mysql))

    print("Tempos de resposta do mongoDB:")
    print(sum(time_exec_mongo))

if __name__ == "__main__":
    main()
