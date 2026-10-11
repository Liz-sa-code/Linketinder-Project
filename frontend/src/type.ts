
export interface Candidato {
    id: string;
    nome: string;
    email: string;
    cpf: string;
    idade: number;
    estado: string;
    cep: string;
    descricao: string;
    formacao: string;
    competencias: string[];
}

export interface Empresa {
    id: string;
    nome: string;
    email: string;
    cnpj: string;
    pais: string;
    estado: string;
    cep: string;
    descricao: string;
}

export interface Vaga {
    id: string;
    empresaId: string;
    titulo: string;
    local: string;
    descricao: string;
    competencias: string[];
}
