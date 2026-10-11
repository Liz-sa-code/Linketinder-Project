
import type { Candidato, Empresa, Vaga } from "./types";

const CHAVES = {
    candidatos: "linketinder_candidatos",
    empresas: "linketinder_empresas",
    vagas: "linketinder_vagas"
};

const candidatosIniciais: Candidato[] = [
    {
        id: "c1",
        nome: "Ana Souza",
        email: "ana@example.com",
        cpf: "000.000.000-00",
        idade: 24,
        estado: "BA",
        cep: "45000-000",
        descricao: "Interesse em desenvolvimento web.",
        formacao: "Sistemas de Informação",
        competencias: ["TypeScript", "JavaScript", "HTML", "CSS"]
    },
    {
        id: "c2",
        nome: "Bruno Lima",
        email: "bruno@example.com",
        cpf: "111.111.111-11",
        idade: 27,
        estado: "BA",
        cep: "40000-000",
        descricao: "Interesse em backend e dados.",
        formacao: "Ciência da Computação",
        competencias: ["Python", "Java", "SQL"]
    },
    {
        id: "c3",
        nome: "Carla Mendes",
        email: "carla@example.com",
        cpf: "222.222.222-22",
        idade: 25,
        estado: "SP",
        cep: "01000-000",
        descricao: "Desenvolvimento de sistemas.",
        formacao: "Engenharia de Software",
        competencias: ["Java", "SQL", "Docker"]
    }
];

const empresasIniciais: Empresa[] = [
    {
        id: "e1",
        nome: "Paçoca Tech",
        email: "rh@pacocatech.example",
        cnpj: "00.000.000/0001-00",
        pais: "Brasil",
        estado: "BA",
        cep: "40000-000",
        descricao: "Empresa demonstrativa de tecnologia."
    }
];

const vagasIniciais: Vaga[] = [
    {
        id: "v1",
        empresaId: "e1",
        titulo: "Desenvolvedor Frontend Júnior",
        local: "Remoto",
        descricao: "Desenvolvimento de interfaces web.",
        competencias: ["TypeScript", "JavaScript", "CSS"]
    },
    {
        id: "v2",
        empresaId: "e1",
        titulo: "Desenvolvedor Backend Júnior",
        local: "Bahia",
        descricao: "Desenvolvimento de APIs e serviços.",
        competencias: ["Java", "SQL"]
    },
    {
        id: "v3",
        empresaId: "e1",
        titulo: "Estágio em Dados",
        local: "Remoto",
        descricao: "Análise e preparação de dados.",
        competencias: ["Python", "SQL"]
    }
];

function ler<T>(chave: string, padrao: T[]): T[] {
    const salvo = localStorage.getItem(chave);

    if (salvo !== null) {
        try {
            return JSON.parse(salvo) as T[];
        } catch {
            localStorage.removeItem(chave);
        }
    }

    localStorage.setItem(chave, JSON.stringify(padrao));
    return padrao;
}

function salvar<T>(chave: string, dados: T[]): void {
    localStorage.setItem(chave, JSON.stringify(dados));
}

// Candidatos

export function listarCandidatos(): Candidato[] {
    return ler(CHAVES.candidatos, candidatosIniciais);
}

export function criarCandidato(candidato: Candidato): void {
    salvar(CHAVES.candidatos, [
        ...listarCandidatos(),
        candidato
    ]);
}

export function excluirCandidato(id: string): void {
    salvar(
        CHAVES.candidatos,
        listarCandidatos().filter(c => c.id !== id)
    );
}

// Empresas

export function listarEmpresas(): Empresa[] {
    return ler(CHAVES.empresas, empresasIniciais);
}

export function criarEmpresa(empresa: Empresa): void {
    salvar(CHAVES.empresas, [
        ...listarEmpresas(),
        empresa
    ]);
}

export function excluirEmpresa(id: string): void {
    salvar(
        CHAVES.empresas,
        listarEmpresas().filter(e => e.id !== id)
    );

    salvar(
        CHAVES.vagas,
        listarVagas().filter(v => v.empresaId !== id)
    );
}

// Vagas

export function listarVagas(): Vaga[] {
    return ler(CHAVES.vagas, vagasIniciais);
}

export function criarVaga(vaga: Vaga): void {
    salvar(CHAVES.vagas, [...listarVagas(), vaga]);
}

export function excluirVaga(id: string): void {
    salvar(
        CHAVES.vagas,
        listarVagas().filter(v => v.id !== id)
    );
}
