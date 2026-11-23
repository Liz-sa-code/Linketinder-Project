export interface PerfilCandidatoAnonimo {
    referencia: string;
    descricao: string;
    competencias: string[];
    formacoes: string[];
}

export interface MatchRecrutador {
    id: number;
    vaga: VagaAnonima;
    candidato: Omit<PerfilCandidatoAnonimo, "referencia">;
}

export interface MatchCandidato {
    id: number;
    vaga: VagaAnonima;
}

export interface VagaAnonima {
    id: number;
    nome: string;
    descricao: string;
    cidade: string;
    estado: string;
    competencias?: string[];
}

export interface VagaEmpresa extends Omit<VagaAnonima, "competencias"> {
    ativa: boolean;
}

export interface RecrutadorEmpresa {
    id: number;
    nome: string;
    areaRecrutamento: string;
    email: string;
}