import socketserver
import mysql.connector
from pymongo import MongoClient
import re, json

class Conexao:
    #tipo 1 SQL e 2 NoSQL
    def realiza(self, tipo_bd, consulta):
        if(tipo_bd==1):
            print("Acessando banco Sql, resposta:")
            resp=self.realizar_conexao_mysqlBD(consulta)
        else:
            print("Acessando banco NoSql, resposta:")
            resp=self.realizar_conexao_mongoBD_D(consulta)

        return resp


    def realizar_conexao_mysqlBD(self, consulta):
    	#Para conectar ao banco de dados deve ser informar os seguintes dados
    	#O Host que é o endereço IP de onde está o banco mysql, porta 3306 é a padrão, caso o banco não esteja usando a porta padrão deve ser informado a porta certa
    	#Database, user e password devem ser informados de acordo com os configurados no banco de dados
        conexao = mysql.connector.connect(
        host='localhost', port='3306', database='school', user='root', password='password')

        cursor = conexao.cursor()
        cursor.execute(consulta)
        resultado = cursor.fetchall()

        cursor.close()
        conexao.close()
        return resultado
        
    def realizar_conexao_mongoBD_D(self,consulta):
        arrayData = consulta.split(".")
        colD = arrayData[1] #pega a collection
        data = re.findall("\[.*\]", consulta)[0] #pega os arqgumentos
        data2 = data.replace(str(colD)+".","")
        
        #Para conectar ao banco de dados deve ser informar os seguintes dados
    	#O Host(no caso localhost) que é o endereço IP de onde está o banco mongodb, porta 27017 é a padrão, caso o banco não esteja usando a porta padrão deve ser informado a porta certa
        myclient = MongoClient("mongodb://localhost:27017/", socketTimeoutMS=1800000, waitQueueTimeoutMS= 1800000, connectTimeoutMS=1800000, serverSelectionTimeoutMS=1800000) 
        db = myclient["school"] 
        collection = db["school"]
        tipo="aggregate"
        consulta = getattr(collection, tipo)
        cursor = consulta(json.loads(data2))
        return list(cursor)
    
#Função para testar se a conexão com os bancos está funcionando    
if __name__ == "__main__":
    con = Conexao()
    con.realizar_conexao_mongoBD_D('''db.school.aggregate([{"$group":{"_id":{},"max_school_absented":{"$max":"$absented"}}},{"$project":{"max_school_absented":1,"_id":0}}])''')
    con.realizar_conexao_mongoBD('''db.school.aggregate([{"$group":{"_id":{},"max_school_absented":{"$max":"$absented"}}},{"$project":{"max_school_absented":1,"_id":0}}])''')
