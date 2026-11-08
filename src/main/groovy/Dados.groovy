
class Dados {

    static List<Candidato> candidatos = [

            new Candidato(
                    "Ana Silva",
                    "ana@email.com",
                    "111.111.111-11",
                    "BA",
                    "45000-000",
                    "Estudante de computação apaixonada por tecnologia",
                    22,
                    [
                            new Competencia("Python"),
                            new Competencia("Java"),
                            new Competencia("Git")
                    ]
            ),

            new Candidato(
                    "Bruno Santos",
                    "bruno@email.com",
                    "222.222.222-22",
                    "SP",
                    "01000-000",
                    "Desenvolvedor backend",
                    25,
                    [
                            new Competencia("Java"),
                            new Competencia("Spring"),
                            new Competencia("PostgreSQL")
                    ]
            ),

            new Candidato(
                    "Carla Oliveira",
                    "carla@email.com",
                    "333.333.333-33",
                    "MG",
                    "30000-000",
                    "Interessada em desenvolvimento web",
                    21,
                    [
                            new Competencia("JavaScript"),
                            new Competencia("Angular"),
                            new Competencia("HTML")
                    ]
            ),

            new Candidato(
                    "Diego Costa",
                    "diego@email.com",
                    "444.444.444-44",
                    "RJ",
                    "20000-000",
                    "Profissional de dados",
                    28,
                    [
                            new Competencia("Python"),
                            new Competencia("SQL"),
                            new Competencia("Data Science")
                    ]
            ),

            new Candidato(
                    "Fernanda Lima",
                    "fernanda@email.com",
                    "555.555.555-55",
                    "PR",
                    "80000-000",
                    "Desenvolvedora full stack",
                    24,
                    [
                            new Competencia("Java"),
                            new Competencia("Angular"),
                            new Competencia("Git")
                    ]
            )
    ]

    static List<Empresa> empresas = [

            new Empresa(
                    "Arroz-Gostoso",
                    "contato@arrozgostoso.com",
                    "11.111.111/0001-11",
                    "BA",
                    "45000-000",
                    "Empresa do setor alimentício",
                    "Brasil",
                    [
                            new Competencia("Java"),
                            new Competencia("Python")
                    ]
            ),

            new Empresa(
                    "Império do Boliche",
                    "rh@imperioboliche.com",
                    "22.222.222/0001-22",
                    "SP",
                    "01000-000",
                    "Empresa de entretenimento",
                    "Brasil",
                    [
                            new Competencia("JavaScript"),
                            new Competencia("Angular")
                    ]
            ),

            new Empresa(
                    "Tech Solutions",
                    "contato@techsolutions.com",
                    "33.333.333/0001-33",
                    "MG",
                    "30000-000",
                    "Empresa de tecnologia",
                    "Brasil",
                    [
                            new Competencia("Java"),
                            new Competencia("Spring")
                    ]
            ),

            new Empresa(
                    "Data Corp",
                    "rh@datacorp.com",
                    "44.444.444/0001-44",
                    "RJ",
                    "20000-000",
                    "Empresa especializada em dados",
                    "Brasil",
                    [
                            new Competencia("Python"),
                            new Competencia("SQL")
                    ]
            ),

            new Empresa(
                    "WebStart",
                    "contato@webstart.com",
                    "55.555.555/0001-55",
                    "PR",
                    "80000-000",
                    "Empresa de desenvolvimento web",
                    "Brasil",
                    [
                            new Competencia("Angular"),
                            new Competencia("JavaScript")
                    ]
            )
    ]

    static void adicionarCandidato(Candidato candidato) {
        if (candidato != null) {
            candidatos.add(candidato)
        }
    }

    static void adicionarEmpresa(Empresa empresa) {
        if (empresa != null) {
            empresas.add(empresa)
        }
    }
}
