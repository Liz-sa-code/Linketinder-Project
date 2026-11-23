import "./style.css";
import type {
    MatchCandidato,
    MatchRecrutador,
    PerfilCandidatoAnonimo,
    RecrutadorEmpresa,
    VagaAnonima,
    VagaEmpresa,
} from "./type";

type TipoConta = "candidato" | "empresa" | "recrutador";
type Pagina =
    | "inicio"
    | "entrar"
    | "cadastro-candidato"
    | "cadastro-empresa"
    | "candidato"
    | "empresa"
    | "recrutador";

interface Sessao {
    token: string;
    tipo: TipoConta;
}

interface LoginResponse extends Sessao {
    id: number;
}

interface IdResponse {
    id: number;
    mensagem: string;
}

interface ListaVagas {
    vagas: VagaAnonima[];
}

interface ListaVagasEmpresa {
    vagas: VagaEmpresa[];
}

interface ListaRecrutadores {
    recrutadores: RecrutadorEmpresa[];
}

interface ListaCandidatos {
    candidatos: PerfilCandidatoAnonimo[];
}

interface ListaMatches<T> {
    matches: T[];
}

const app = document.querySelector<HTMLDivElement>("#app");
if (!app) {
    throw new Error("Elemento principal da aplicação não encontrado.");
}

let pagina: Pagina = "inicio";
let sessao: Sessao | null = null;
let mensagem = "";
let erro = false;

function escapar(valor: unknown): string {
    return String(valor ?? "").replace(/[&<>"']/g, (caractere) => {
        const entidades: Record<string, string> = {
            "&": "&amp;",
            "<": "&lt;",
            ">": "&gt;",
            '"': "&quot;",
            "'": "&#39;",
        };
        return entidades[caractere];
    });
}

async function requisicao<T>(caminho: string, init: RequestInit = {}): Promise<T> {
    const headers = new Headers(init.headers);
    if (init.body) {
        headers.set("Content-Type", "application/json");
    }
    if (sessao) {
        headers.set("Authorization", `Bearer ${sessao.token}`);
    }

    const resposta = await fetch(`/api${caminho}`, { ...init, headers });
    const texto = await resposta.text();
    const dados: unknown = texto ? JSON.parse(texto) : {};
    if (!resposta.ok) {
        const detalhe =
            typeof dados === "object" &&
            dados !== null &&
            "erro" in dados &&
            typeof dados.erro === "string"
                ? dados.erro
                : `A requisição falhou (${resposta.status}).`;
        throw new Error(detalhe);
    }
    return dados as T;
}

function dadosFormulario(formulario: HTMLFormElement): Record<string, string> {
    const formData = new FormData(formulario);
    return Object.fromEntries(
        [...formData.entries()].map(([chave, valor]) => [
            chave,
            String(valor).trim(),
        ]),
    );
}

function mostrarMensagem(texto: string, houveErro = false): void {
    mensagem = texto;
    erro = houveErro;
    void renderizar();
}

function navegacao(): string {
    const botoes: Array<[Pagina, string]> = [
        ["inicio", "Início"],
        ["cadastro-candidato", "Sou candidato"],
        ["cadastro-empresa", "Sou empresa"],
    ];
    if (sessao?.tipo === "candidato") botoes.push(["candidato", "Minhas vagas"]);
    if (sessao?.tipo === "empresa") botoes.push(["empresa", "Painel da empresa"]);
    if (sessao?.tipo === "recrutador") {
        botoes.push(["recrutador", "Painel do recrutador"]);
    }
    if (sessao) {
        botoes.push(["inicio", "Sair"]);
    } else {
        botoes.push(["entrar", "Entrar"]);
    }

    return botoes
        .map(
            ([destino, rotulo]) =>
                `<button class="nav-button${pagina === destino ? " selecionado" : ""}" data-pagina="${destino}">${rotulo}</button>`,
        )
        .join("");
}

function layout(conteudo: string): void {
    if (!app) return;
    app.innerHTML = `
        <header class="cabecalho">
            <a class="marca" href="#" data-pagina="inicio"><span class="marca-icone">L</span> Linketinder</a>
            <nav class="navegacao">${navegacao()}</nav>
        </header>
        <main class="container">${conteudo}</main>
        <footer>Linketinder — conexões profissionais anônimas</footer>
    `;

    app.querySelectorAll<HTMLElement>("[data-pagina]").forEach((elemento) => {
        elemento.addEventListener("click", (evento) => {
            evento.preventDefault();
            const destino = elemento.dataset.pagina as Pagina | undefined;
            if (elemento.textContent?.trim() === "Sair") {
                sessao = null;
            }
            if (destino) {
                pagina = destino;
                mensagem = "";
                void renderizar();
            }
        });
    });
}

function caixaMensagem(): string {
    if (!mensagem) return "";
    return `<p class="${erro ? "mensagem-erro" : "mensagem"}" role="status">${escapar(mensagem)}</p>`;
}

function formulario(
    titulo: string,
    descricao: string,
    conteudo: string,
    id: string,
    botao: string,
): string {
    return `
        <section class="titulo-pagina"><span class="etiqueta">LINKETINDER</span><h1>${titulo}</h1><p>${descricao}</p></section>
        <form class="formulario" id="${id}">
            ${caixaMensagem()}
            ${conteudo}
            <button class="botao primario" type="submit">${botao}</button>
        </form>
    `;
}

function campo(
    nome: string,
    rotulo: string,
    tipo = "text",
    obrigatorio = true,
): string {
    return `<label>${rotulo}<input name="${nome}" type="${tipo}" ${obrigatorio ? "required" : ""}></label>`;
}

function areaTexto(nome: string, rotulo: string, obrigatorio = true): string {
    return `<label>${rotulo}<textarea name="${nome}" rows="4" ${obrigatorio ? "required" : ""}></textarea></label>`;
}

function cartaoVaga(vaga: VagaAnonima, acao = ""): string {
    const competencias = (vaga.competencias ?? [])
        .map((item) => `<span>${escapar(item)}</span>`)
        .join("");
    return `
        <article class="cartao vaga">
            <h2>${escapar(vaga.nome)}</h2>
            <p>${escapar(vaga.descricao)}</p>
            <p>${escapar([vaga.cidade, vaga.estado].filter(Boolean).join(" — "))}</p>
            <div class="competencias">${competencias}</div>
            ${acao}
        </article>
    `;
}

function inicio(): string {
    return `
        <section class="hero">
            <div><span class="etiqueta">ENCONTRE SUA PRÓXIMA CONEXÃO</span>
                <h1>Talento e oportunidade, sem vieses de identidade.</h1>
                <p>Candidatos e recrutadores descobrem afinidades profissionais. A identidade permanece anônima antes e depois do match.</p>
                <div class="acoes">
                    <button class="botao primario" data-pagina="cadastro-candidato">Cadastrar como candidato</button>
                    <button class="botao secundario" data-pagina="cadastro-empresa">Cadastrar empresa</button>
                </div>
            </div>
            <div class="ilustracao"><div class="cartao-ilustracao"><span class="estrela">✦</span><h2>Interesse mútuo</h2><p>Match profissional com anonimato permanente.</p></div></div>
        </section>
        <section class="secao"><h2>Vagas disponíveis</h2><div id="vagas-inicio" class="grade-vagas"><p>Carregando vagas...</p></div></section>
    `;
}

async function carregarInicio(): Promise<void> {
    try {
        const { vagas } = await requisicao<ListaVagas>("/vagas");
        const lista = app?.querySelector<HTMLDivElement>("#vagas-inicio");
        if (lista) {
            lista.innerHTML = vagas.length
                ? vagas.map((vaga) => cartaoVaga(vaga)).join("")
                : '<p class="vazio">Não há vagas disponíveis no momento.</p>';
        }
    } catch (falha) {
        const lista = app?.querySelector<HTMLDivElement>("#vagas-inicio");
        if (lista) lista.innerHTML = `<p class="vazio">${escapar(mensagemErro(falha))}</p>`;
    }
}

function paginaEntrar(): string {
    return formulario(
        "Acesse sua conta",
        "Entre como candidato, empresa ou recrutador.",
        `
            <label>Tipo de conta
                <select name="tipo" required>
                    <option value="candidato">Candidato</option>
                    <option value="empresa">Empresa</option>
                    <option value="recrutador">Recrutador</option>
                </select>
            </label>
            ${campo("email", "E-mail", "email")}
            ${campo("senha", "Senha", "password")}
        `,
        "form-entrar",
        "Entrar",
    );
}

function paginaCadastroCandidato(): string {
    return formulario(
        "Cadastro de candidato",
        "Seus dados pessoais não serão mostrados às empresas ou recrutadores.",
        `
            <div class="duas-colunas">
                ${campo("nome", "Nome")}
                ${campo("sobrenome", "Sobrenome")}
                ${campo("data_nascimento", "Data de nascimento", "date")}
                ${campo("email", "E-mail", "email")}
                ${campo("cpf", "CPF")}
                ${campo("cep", "CEP")}
                ${campo("pais", "País")}
                ${campo("senha", "Senha", "password")}
            </div>
            ${areaTexto("descricao", "Descrição profissional")}
            <label>Competências (separadas por vírgula)
                <input name="competencias" placeholder="Java, SQL, comunicação">
            </label>
        `,
        "form-candidato",
        "Cadastrar",
    );
}

function paginaCadastroEmpresa(): string {
    return formulario(
        "Cadastro de empresa",
        "Cadastre a empresa para publicar vagas e gerenciar recrutadores.",
        `
            ${campo("nome", "Nome da empresa")}
            ${campo("cnpj", "CNPJ")}
            ${campo("email", "E-mail", "email")}
            ${campo("senha", "Senha", "password")}
            ${areaTexto("descricao", "Descrição")}
            <div class="duas-colunas">${campo("pais", "País")}${campo("cep", "CEP")}</div>
        `,
        "form-empresa",
        "Cadastrar empresa",
    );
}

function mensagemErro(falha: unknown): string {
    return falha instanceof Error ? falha.message : "Não foi possível concluir a operação.";
}

function exigirSessao(tipo: TipoConta): boolean {
    if (sessao?.tipo === tipo) return true;
    layout(`<section class="vazio"><h1>Acesso à área restrita</h1><p>Entre com uma conta do tipo correspondente para continuar.</p><button class="botao primario" data-pagina="entrar">Entrar</button></section>`);
    return false;
}

async function paginaCandidato(): Promise<void> {
    if (!exigirSessao("candidato")) return;
    layout('<section class="titulo-pagina"><span class="etiqueta">CANDIDATO</span><h1>Vagas e matches</h1><p>Seu perfil de identidade não é compartilhado com a empresa ou o recrutador.</p></section><div id="painel-candidato"><p>Carregando...</p></div>');
    try {
        const [vagasResultado, matchesResultado] = await Promise.all([
            requisicao<ListaVagas>("/vagas"),
            requisicao<ListaMatches<MatchCandidato>>("/matches"),
        ]);
        const painel = app?.querySelector<HTMLDivElement>("#painel-candidato");
        if (!painel) return;
        const vagas = vagasResultado.vagas
            .map((vaga) =>
                cartaoVaga(
                    vaga,
                    `<button class="botao primario" data-interesse-vaga="${vaga.id}">Tenho interesse</button>`,
                ),
            )
            .join("");
        const matches = matchesResultado.matches
            .map((match) =>
                cartaoVaga(
                    match.vaga,
                    '<p class="anonimo">Match confirmado. A identidade de ambas as partes permanece anônima.</p>',
                ),
            )
            .join("");
        painel.innerHTML = `
            ${caixaMensagem()}
            <section class="secao"><h2>Vagas ativas</h2><div class="grade-vagas">${vagas || '<p class="vazio">Não há vagas ativas.</p>'}</div></section>
            <section class="secao"><h2>Meus matches</h2><div class="grade-vagas">${matches || '<p class="vazio">Ainda não há matches.</p>'}</div></section>
        `;
        painel.querySelectorAll<HTMLButtonElement>("[data-interesse-vaga]").forEach((botao) => {
            botao.addEventListener("click", async () => {
                try {
                    await requisicao("/candidatos/interesses", {
                        method: "POST",
                        body: JSON.stringify({ vagaId: botao.dataset.interesseVaga }),
                    });
                    mostrarMensagem("Interesse registrado.");
                } catch (falha) {
                    mostrarMensagem(mensagemErro(falha), true);
                }
            });
        });
    } catch (falha) {
        layout(`<p class="vazio">${escapar(mensagemErro(falha))}</p>`);
    }
}

async function paginaEmpresa(): Promise<void> {
    if (!exigirSessao("empresa")) return;
    layout('<section class="titulo-pagina"><span class="etiqueta">EMPRESA</span><h1>Painel da empresa</h1><p>Publique vagas e conceda permissões por vaga aos recrutadores da empresa.</p></section><div id="painel-empresa"><p>Carregando...</p></div>');
    try {
        const [vagasResultado, recrutadoresResultado] = await Promise.all([
            requisicao<ListaVagasEmpresa>("/empresa/vagas"),
            requisicao<ListaRecrutadores>("/empresa/recrutadores"),
        ]);
        const vagas = vagasResultado.vagas;
        const recrutadores = recrutadoresResultado.recrutadores;
        const painel = app?.querySelector<HTMLDivElement>("#painel-empresa");
        if (!painel) return;
        painel.innerHTML = `
            ${caixaMensagem()}
            <section class="painel"><h2>Criar vaga</h2>
                <form id="form-vaga">
                    <div class="duas-colunas">${campo("nome", "Título da vaga")}${campo("cidade", "Cidade")}${campo("estado", "Estado")}</div>
                    ${areaTexto("descricao", "Descrição")}
                    <label>Competências (separadas por vírgula)<input name="competencias"></label>
                    <button class="botao primario" type="submit">Publicar vaga</button>
                </form>
            </section>
            <section class="painel"><h2>Cadastrar recrutador</h2>
                <form id="form-recrutador">
                    <div class="duas-colunas">
                        ${campo("nome", "Nome")}${campo("area_recrutamento", "Área de recrutamento")}
                        ${campo("email", "E-mail", "email")}${campo("cpf", "CPF")}
                        ${campo("data_nascimento", "Data de nascimento", "date")}${campo("senha", "Senha", "password")}
                    </div>
                    ${areaTexto("descricao", "Descrição")}
                    <button class="botao primario" type="submit">Cadastrar recrutador</button>
                </form>
            </section>
            <section class="painel"><h2>Permissões por vaga</h2>
                <form id="form-permissao">
                    <label>Recrutador<select name="recrutadorId" required>${recrutadores.map((r) => `<option value="${r.id}">${escapar(r.nome)} — ${escapar(r.email)}</option>`).join("")}</select></label>
                    <label>Vaga<select name="vagaId" required>${vagas.map((v) => `<option value="${v.id}">${escapar(v.nome)}</option>`).join("")}</select></label>
                    <button class="botao primario" type="submit">Conceder permissão</button>
                </form>
                ${recrutadores.map((r) => `
                    <article class="cartao"><h3>${escapar(r.nome)}</h3><p>${escapar(r.areaRecrutamento)} · ${escapar(r.email)}</p>
                        ${(vagas).map((v) => `<button class="botao-excluir" data-revogar-recrutador="${r.id}" data-revogar-vaga="${v.id}">Revogar ${escapar(v.nome)}</button>`).join(" ")}
                    </article>
                `).join("")}
            </section>
            <section class="secao"><h2>Vagas da empresa</h2><div class="grade-vagas">${vagas.map((vaga) => cartaoVaga(vaga)).join("") || '<p class="vazio">Ainda não há vagas.</p>'}</div></section>
        `;
        conectarFormularioVaga();
        conectarFormularioRecrutador();
        conectarFormularioPermissao();
        painel.querySelectorAll<HTMLButtonElement>("[data-revogar-recrutador]").forEach((botao) => {
            botao.addEventListener("click", async () => {
                try {
                    await requisicao("/permissoes", {
                        method: "DELETE",
                        body: JSON.stringify({
                            recrutadorId: botao.dataset.revogarRecrutador,
                            vagaId: botao.dataset.revogarVaga,
                        }),
                    });
                    mostrarMensagem("Permissão removida.");
                } catch (falha) {
                    mostrarMensagem(mensagemErro(falha), true);
                }
            });
        });
    } catch (falha) {
        layout(`<p class="vazio">${escapar(mensagemErro(falha))}</p>`);
    }
}

function conectarFormularioVaga(): void {
    app?.querySelector<HTMLFormElement>("#form-vaga")?.addEventListener("submit", async (evento) => {
        evento.preventDefault();
        const formulario = evento.currentTarget as HTMLFormElement;
        const valores = dadosFormulario(formulario);
        try {
            await requisicao<IdResponse>("/vagas", {
                method: "POST",
                body: JSON.stringify({
                    vaga: {
                        nome: valores.nome,
                        descricao: valores.descricao,
                        cidade: valores.cidade,
                        estado: valores.estado,
                        ativa: true,
                    },
                    competencias: valores.competencias.split(",").map((item) => item.trim()).filter(Boolean),
                }),
            });
            mostrarMensagem("Vaga publicada.");
        } catch (falha) {
            mostrarMensagem(mensagemErro(falha), true);
        }
    });
}

function conectarFormularioRecrutador(): void {
    app?.querySelector<HTMLFormElement>("#form-recrutador")?.addEventListener("submit", async (evento) => {
        evento.preventDefault();
        const valores = dadosFormulario(evento.currentTarget as HTMLFormElement);
        try {
            await requisicao<IdResponse>("/recrutadores", {
                method: "POST",
                body: JSON.stringify({
                    recrutador: {
                        nome: valores.nome,
                        area_recrutamento: valores.area_recrutamento,
                        descricao: valores.descricao,
                        email: valores.email,
                        cpf: valores.cpf,
                        data_nascimento: valores.data_nascimento,
                        senha: valores.senha,
                    },
                }),
            });
            mostrarMensagem("Recrutador cadastrado.");
        } catch (falha) {
            mostrarMensagem(mensagemErro(falha), true);
        }
    });
}

function conectarFormularioPermissao(): void {
    app?.querySelector<HTMLFormElement>("#form-permissao")?.addEventListener("submit", async (evento) => {
        evento.preventDefault();
        const valores = dadosFormulario(evento.currentTarget as HTMLFormElement);
        try {
            await requisicao("/permissoes", {
                method: "POST",
                body: JSON.stringify({
                    recrutadorId: valores.recrutadorId,
                    vagaId: valores.vagaId,
                }),
            });
            mostrarMensagem("Permissão concedida.");
        } catch (falha) {
            mostrarMensagem(mensagemErro(falha), true);
        }
    });
}

async function paginaRecrutador(): Promise<void> {
    if (!exigirSessao("recrutador")) return;
    layout('<section class="titulo-pagina"><span class="etiqueta">RECRUTADOR</span><h1>Perfis anônimos e matches</h1><p>Você verá candidatos que demonstraram interesse em vagas da sua empresa. Só poderá analisar vagas autorizadas. Dados de identidade não são disponibilizados.</p></section><div id="painel-recrutador"><p>Carregando...</p></div>');
    try {
        const [vagasResultado, candidatosResultado, matchesResultado] = await Promise.all([
            requisicao<ListaVagas>("/recrutadores/vagas"),
            requisicao<ListaCandidatos>("/recrutadores/candidatos"),
            requisicao<ListaMatches<MatchRecrutador>>("/matches"),
        ]);
        const vagas = vagasResultado.vagas;
        const painel = app?.querySelector<HTMLDivElement>("#painel-recrutador");
        if (!painel) return;
        const candidatos = candidatosResultado.candidatos
            .map((candidato) => `
                <article class="cartao">
                    <span class="etiqueta">PERFIL ANÔNIMO</span>
                    <p>${escapar(candidato.descricao)}</p>
                    <div class="competencias">${candidato.competencias.map((item) => `<span>${escapar(item)}</span>`).join("")}</div>
                    ${candidato.formacoes.length ? `<p><strong>Formação:</strong> ${candidato.formacoes.map(escapar).join("; ")}</p>` : ""}
                    <label>Demonstrar interesse para vaga
                        <select data-vaga-perfil="${escapar(candidato.referencia)}" ${vagas.length ? "" : "disabled"}>
                            ${vagas.map((vaga) => `<option value="${vaga.id}">${escapar(vaga.nome)}</option>`).join("")}
                        </select>
                    </label>
                    <button class="botao primario" data-interesse-perfil="${escapar(candidato.referencia)}" ${vagas.length ? "" : "disabled"}>Tenho interesse</button>
                </article>
            `)
            .join("");
        const matches = matchesResultado.matches
            .map((match) => `
                <article class="cartao">
                    <span class="etiqueta">MATCH ANÔNIMO</span>
                    <h2>${escapar(match.vaga.nome)}</h2>
                    <p>${escapar(match.vaga.descricao)}</p>
                    <p>${escapar(match.candidato.descricao)}</p>
                    <div class="competencias">${match.candidato.competencias.map((item) => `<span>${escapar(item)}</span>`).join("")}</div>
                    ${match.candidato.formacoes.length ? `<p><strong>Formação:</strong> ${match.candidato.formacoes.map(escapar).join("; ")}</p>` : ""}
                </article>
            `)
            .join("");
        painel.innerHTML = `
            ${caixaMensagem()}
            <section class="secao"><h2>Vagas autorizadas (${vagas.length})</h2><div class="grade-vagas">${vagas.map((vaga) => cartaoVaga(vaga)).join("") || '<p class="vazio">A empresa ainda não concedeu acesso a vagas.</p>'}</div></section>
            <section class="secao"><h2>Candidatos interessados em vagas da empresa</h2><div class="grade-vagas">${candidatos || '<p class="vazio">Não há perfis disponíveis.</p>'}</div></section>
            <section class="secao"><h2>Matches</h2><div class="grade-vagas">${matches || '<p class="vazio">Ainda não há matches.</p>'}</div></section>
        `;
        painel.querySelectorAll<HTMLButtonElement>("[data-interesse-perfil]").forEach((botao) => {
            botao.addEventListener("click", async () => {
                const referencia = botao.dataset.interessePerfil;
                const seletor = painel.querySelector<HTMLSelectElement>(`[data-vaga-perfil="${CSS.escape(referencia ?? "")}"]`);
                if (!referencia || !seletor) return;
                try {
                    await requisicao("/recrutadores/interesses", {
                        method: "POST",
                        body: JSON.stringify({ referencia, vagaId: seletor.value }),
                    });
                    mostrarMensagem("Interesse registrado.");
                } catch (falha) {
                    mostrarMensagem(mensagemErro(falha), true);
                }
            });
        });
    } catch (falha) {
        layout(`<p class="vazio">${escapar(mensagemErro(falha))}</p>`);
    }
}

async function conectarFormularioLogin(): Promise<void> {
    app?.querySelector<HTMLFormElement>("#form-entrar")?.addEventListener("submit", async (evento) => {
        evento.preventDefault();
        const valores = dadosFormulario(evento.currentTarget as HTMLFormElement);
        try {
            const resposta = await requisicao<LoginResponse>("/auth/login", {
                method: "POST",
                body: JSON.stringify(valores),
            });
            sessao = { token: resposta.token, tipo: resposta.tipo };
            pagina =
                resposta.tipo === "candidato"
                    ? "candidato"
                    : resposta.tipo === "empresa"
                      ? "empresa"
                      : "recrutador";
            mensagem = "";
            await renderizar();
        } catch (falha) {
            mostrarMensagem(mensagemErro(falha), true);
        }
    });
}

async function conectarCadastroCandidato(): Promise<void> {
    app?.querySelector<HTMLFormElement>("#form-candidato")?.addEventListener("submit", async (evento) => {
        evento.preventDefault();
        const valores = dadosFormulario(evento.currentTarget as HTMLFormElement);
        try {
            const dados = { ...valores };
            delete dados.competencias;
            const resposta = await requisicao<IdResponse>("/candidatos", {
                method: "POST",
                body: JSON.stringify({
                    dados,
                    competencias: valores.competencias.split(",").map((item) => item.trim()).filter(Boolean),
                }),
            });
            mostrarMensagem(resposta.mensagem);
        } catch (falha) {
            mostrarMensagem(mensagemErro(falha), true);
        }
    });
}

async function conectarCadastroEmpresa(): Promise<void> {
    app?.querySelector<HTMLFormElement>("#form-empresa")?.addEventListener("submit", async (evento) => {
        evento.preventDefault();
        const valores = dadosFormulario(evento.currentTarget as HTMLFormElement);
        try {
            const resposta = await requisicao<IdResponse>("/empresas", {
                method: "POST",
                body: JSON.stringify({ empresa: valores }),
            });
            mostrarMensagem(resposta.mensagem);
        } catch (falha) {
            mostrarMensagem(mensagemErro(falha), true);
        }
    });
}

async function renderizar(): Promise<void> {
    if (pagina === "inicio") {
        layout(inicio());
        await carregarInicio();
        return;
    }
    if (pagina === "entrar") {
        layout(paginaEntrar());
        await conectarFormularioLogin();
        return;
    }
    if (pagina === "cadastro-candidato") {
        layout(paginaCadastroCandidato());
        await conectarCadastroCandidato();
        return;
    }
    if (pagina === "cadastro-empresa") {
        layout(paginaCadastroEmpresa());
        await conectarCadastroEmpresa();
        return;
    }
    if (pagina === "candidato") {
        await paginaCandidato();
        return;
    }
    if (pagina === "empresa") {
        await paginaEmpresa();
        return;
    }
    await paginaRecrutador();
}

void renderizar();
