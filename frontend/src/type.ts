export interface Candidato {
    id: number;
    nome: string;
    sobrenome: string;
    data_nascimento: string;
    email: string;
    cpf: string;
    pais: string;
    cep: string;
    descricao: string;
    formacao: string;
    competencias: string[];
}

export interface Empresa {
    id: number;
    nome: string;
    cnpj: string;
    email: string;
    descricao: string;
    pais: string;
    cep: string;
}

export interface Vaga {
    id: number;
    empresa_id: number;
    nome: string;
    descricao: string;
    cidade: string;
    estado: string;
    ativa: boolean;
    empresa?: string;
    competencias: string[];
}