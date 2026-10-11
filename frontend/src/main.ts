
import Chart from "chart.js/auto";

import type { Candidato, Empresa, Vaga } from "./types";

import {
    listarCandidatos,
    criarCandidato,
    excluirCandidato,
    listarEmpresas,
    criarEmpresa,
    excluirEmpresa,
    listarVagas,
    criarVaga,
    excluirVaga
} from "./data";

import "./style.css";

const app = document.querySelector<HTMLDivElement>("#app");

if (!app) {
    throw new Error("Elemento #app não encontrado.");
}

type Tela =
    | "inicio"
    | "cadastro-candidato"
    | "cadastro-empresa"
    | "perfil-candidato"
    | "perfil-empresa";

let telaAtual: Tela = "inicio";
let grafico: Chart | undefined;

function gerarId(): string {
    return crypto.randomUUID();
}

function escapar(valor: string): string {
    return valor.replace(/[&<>"']/g, (caractere) => {
        const entidades: Record<string, string> = {
            "&": "&amp;",
            "<": "&lt;",
            ">": "&gt;",
            '"': "&quot;",
            "'": "&#039;"
        };

        return entidades[caractere];
    });
}

function competencias(valor: string): string[] {
    return [
        ...new Set(
            valor.split(",")
                .map(item => item.trim())
                .filter(Boolean)
        )
    ];
}

function botoesNavegacao(): string {
    const links: [Tela, string][] = [
        ["inicio", "Início"],
        ["cadastro-candidato", "Cadastrar candidato"],
        ["cadastro-empresa", "Cadastrar empresa"],
        ["perfil-candidato", "Vagas"],
        ["perfil-empresa", "Talentos"]
    ];

    return links.map(([tela, texto]) => `
        <button
            class="nav-button ${telaAtual === tela ? "selecionado" : ""}"
            data-tela="${tela}"
        >
            ${texto}
        </button>
    `).join("");
}

function estrutura(conteudo: string): void {
    app!.innerHTML = `
        <header class="cabecalho">
            <a href="#" class="marca" data-tela="inicio">
                <span class="marca-icone">L</span>
                <span>Linketinder</span>
            </a>

            <nav class="navegacao">
                ${botoesNavegacao()}
            </nav>
        </header>

        <main class="container">
            ${conteudo}
        </main>

        <footer>
            Linketinder · Protótipo acadêmico em TypeScript
        </footer>
    `;
}

function navegar(tela: Tela): void {
    grafico?.destroy();
    grafico = undefined;
    telaAtual = tela;

    switch (tela) {
        case "inicio":
            inicio();
            break;
        case "cadastro-candidato":
            telaCadastroCandidato();
            break;
        case "cadastro-empresa":
            telaCadastroEmpresa();
            break;
        case "perfil-candidato":
            perfilCandidato();
            break;
        case "perfil-empresa":
            perfilEmpresa();
            break;
    }
}

function inicio(): void {
    estrutura(`
        <section class="hero">
            <div>
                <span class="etiqueta">TALENTOS E OPORTUNIDADES</span>
                <h1>O próximo passo da sua carreira começa aqui.</h1>
                <p>
                    Conectamos competências, formação e oportunidades
                    para aproximar candidatos e empresas.
                </p>

                <div class="acoes">
                    <button class="botao primario"
                        data-tela="cadastro-candidato">
                        Sou candidato
                    </button>

                    <button class="botao secundario"
                        data-tela="cadastro-empresa">
                        Sou empresa
                    </button>
                </div>
            </div>

            <div class="ilustracao">
                <div class="cartao-ilustracao">
                    <span class="estrela">✦</span>
                    <h2>Match de talentos</h2>
                    <p>Competências que conectam.</p>
                </div>
            </div>
        </section>

        <section class="resumos">
            <article class="resumo">
                <span>Candidatos</span>
                <strong>${listarCandidatos().length}</strong>
            </article>

            <article class="resumo">
                <span>Empresas</span>
                <strong>${listarEmpresas().length}</strong>
            </article>

            <article class="resumo">
                <span>Vagas</span>
                <strong>${listarVagas().length}</strong>
            </article>
        </section>

        <section class="secao">
            <h2>Explore o Linketinder</h2>

            <div class="grade">
                <article class="cartao">
                    <h3>Perfil do candidato</h3>
                    <p>Consulte vagas e competências desejadas.</p>
                    <button class="botao secundario"
                        data-tela="perfil-candidato">
                        Consultar vagas
                    </button>
                </article>

                <article class="cartao">
                    <h3>Perfil da empresa</h3>
                    <p>Visualize candidatos anônimos e estatísticas.</p>
                    <button class="botao secundario"
                        data-tela="perfil-empresa">
                        Consultar talentos
                    </button>
                </article>
            </div>
        </section>
    `);
}

// Cadastro de candidato

function telaCadastroCandidato(): void {
    estrutura(`
        <section class="titulo-pagina">
            <span class="etiqueta">CADASTRO</span>
            <h1>Crie seu perfil de candidato</h1>
            <p>Apresente sua formação e suas competências.</p>
        </section>

        <form id="form-candidato" class="formulario">
            <label>
                Nome completo *
                <input name="nome" required maxlength="100">
            </label>

            <label>
                E-mail *
                <input name="email" type="email" required maxlength="150">
            </label>

            <div class="duas-colunas">
                <label>
                    CPF *
                    <input name="cpf" required maxlength="14">
                </label>

                <label>
                    Idade *
                    <input name="idade" type="number"
                        min="16" max="100" required>
                </label>
            </div>

            <div class="duas-colunas">
                <label>
                    Estado (UF) *
                    <input name="estado" maxlength="2" required>
                </label>

                <label>
                    CEP *
                    <input name="cep" maxlength="9" required>
                </label>
            </div>

            <label>
                Formação *
                <input name="formacao" required maxlength="160">
            </label>

            <label>
                Competências *
                <input name="competencias" required
                    placeholder="Python, Java, SQL">
                <small>Separe as competências por vírgulas.</small>
            </label>

            <label>
                Descrição pessoal
                <textarea name="descricao" rows="4" maxlength="500"></textarea>
            </label>

            <button class="botao primario" type="submit">
                Cadastrar candidato
            </button>

            <p id="mensagem-candidato" class="mensagem"></p>
        </form>
    `);

    document.querySelector<HTMLFormElement>("#form-candidato")!
        .addEventListener("submit", (evento) => {
            evento.preventDefault();

            const form = evento.currentTarget as HTMLFormElement;
            const dados = new FormData(form);

            const candidato: Candidato = {
                id: gerarId(),
                nome: String(dados.get("nome")).trim(),
                email: String(dados.get("email")).trim(),
                cpf: String(dados.get("cpf")).trim(),
                idade: Number(dados.get("idade")),
                estado: String(dados.get("estado")).trim().toUpperCase(),
                cep: String(dados.get("cep")).trim(),
                formacao: String(dados.get("formacao")).trim(),
                competencias: competencias(
                    String(dados.get("competencias"))
                ),
                descricao: String(dados.get("descricao") ?? "").trim()
            };

            criarCandidato(candidato);
            form.reset();

            document.querySelector("#mensagem-candidato")!
                .textContent = "Candidato cadastrado com sucesso!";
        });
}

// Cadastro de empresa

function telaCadastroEmpresa(): void {
    estrutura(`
        <section class="titulo-pagina">
            <span class="etiqueta">CADASTRO</span>
            <h1>Cadastre sua empresa</h1>
            <p>Encontre pessoas com as competências que sua equipe precisa.</p>
        </section>

        <form id="form-empresa" class="formulario">
            <label>
                Nome da empresa *
                <input name="nome" required maxlength="100">
            </label>

            <label>
                E-mail corporativo *
                <input name="email" type="email" required maxlength="150">
            </label>

            <label>
                CNPJ *
                <input name="cnpj" required maxlength="18">
            </label>

            <label>
                País *
                <input name="pais" value="Brasil" required maxlength="60">
            </label>

            <div class="duas-colunas">
                <label>
                    Estado (UF) *
                    <input name="estado" required maxlength="2">
                </label>

                <label>
                    CEP *
                    <input name="cep" required maxlength="9">
                </label>
            </div>

            <label>
                Descrição da empresa *
                <textarea name="descricao" required rows="4"
                    maxlength="500"></textarea>
            </label>

            <button class="botao primario" type="submit">
                Cadastrar empresa
            </button>

            <p id="mensagem-empresa" class="mensagem"></p>
        </form>
    `);

    document.querySelector<HTMLFormElement>("#form-empresa")!
        .addEventListener("submit", (evento) => {
            evento.preventDefault();

            const form = evento.currentTarget as HTMLFormElement;
            const dados = new FormData(form);

            const empresa: Empresa = {
                id: gerarId(),
                nome: String(dados.get("nome")).trim(),
                email: String(dados.get("email")).trim(),
                cnpj: String(dados.get("cnpj")).trim(),
                pais: String(dados.get("pais")).trim(),
                estado: String(dados.get("estado")).trim().toUpperCase(),
                cep: String(dados.get("cep")).trim(),
                descricao: String(dados.get("descricao")).trim()
            };

            criarEmpresa(empresa);
            form.reset();

            document.querySelector("#mensagem-empresa")!
                .textContent = "Empresa cadastrada com sucesso!";
        });
}

// Perfil do candidato

function perfilCandidato(): void {
    estrutura(`
        <section class="titulo-pagina">
            <span class="etiqueta">ÁREA DO CANDIDATO</span>
            <h1>Vagas disponíveis</h1>
            <p>Encontre oportunidades que combinam com seu perfil.</p>
        </section>

        <label class="busca">
            Buscar vaga
            <input id="busca-vagas"
                placeholder="Digite o cargo ou uma competência">
        </label>

        <div id="lista-vagas" class="grade-vagas"></div>
    `);

    const busca = document.querySelector<HTMLInputElement>("#busca-vagas")!;

    const atualizar = (): void => {
        const consulta = busca.value.toLocaleLowerCase("pt-BR");

        const filtradas = listarVagas().filter((vaga) => {
            const empresa = listarEmpresas()
                .find((item) => item.id === vaga.empresaId);

            const texto = [
                vaga.titulo,
                vaga.local,
                vaga.descricao,
                ...vaga.competencias
            ].join(" ").toLocaleLowerCase("pt-BR");

            return texto.includes(consulta) && Boolean(empresa);
        });

        document.querySelector("#lista-vagas")!.innerHTML =
            filtradas.length
                ? filtradas.map((vaga) => `
                    <article class="cartao vaga">
                        <span class="etiqueta">OPORTUNIDADE</span>
                        <h2>${escapar(vaga.titulo)}</h2>
                        <p>📍 ${escapar(vaga.local)}</p>
                        <p>${escapar(vaga.descricao)}</p>

                        <div class="competencias">
                            ${vaga.competencias.map(skill =>
                    `<span>${escapar(skill)}</span>`
                ).join("")}
                        </div>

                        <p class="anonimo">
                            Empresa confidencial até o match
                        </p>
                    </article>
                `).join("")
                : `<p class="vazio">Nenhuma vaga encontrada.</p>`;
    };

    busca.addEventListener("input", atualizar);
    atualizar();
}

// Perfil da empresa


function perfilEmpresa(): void {
    const lista = listarCandidatos();
    const empresas = listarEmpresas();

    estrutura(`
        <section class="titulo-pagina">
            <span class="etiqueta">ÁREA DA EMPRESA</span>
            <h1>Banco de talentos</h1>
            <p>Explore formações e competências de maneira anônima.</p>
        </section>

        <section class="painel">
            <div>
                <span class="etiqueta">ANÁLISE DE COMPETÊNCIAS</span>
                <h2>Candidatos por competência</h2>
                <p>Quantidade de candidatos que informaram cada habilidade.</p>
            </div>
            <div class="grafico">
                <canvas id="grafico-competencias"></canvas>
            </div>
        </section>

        <section class="secao">
            <h2>Publicar nova vaga</h2>

            ${
        empresas.length > 0
            ? `
                    <form id="form-vaga" class="formulario">
                        <label>
                            Empresa responsável *
                            <select name="empresaId" required>
                                ${empresas.map(empresa => `
                                    <option value="${escapar(empresa.id)}">
                                        ${escapar(empresa.nome)}
                                    </option>
                                `).join("")}
                            </select>
                        </label>

                        <label>
                            Título da vaga *
                            <input name="titulo"
                                placeholder="Ex.: Desenvolvedor Frontend"
                                required>
                        </label>

                        <label>
                            Localidade *
                            <input name="local"
                                placeholder="Ex.: Remoto ou Bahia"
                                required>
                        </label>

                        <label>
                            Competências desejadas *
                            <input name="competencias"
                                placeholder="Ex.: TypeScript, Java, SQL"
                                required>
                        </label>

                        <label>
                            Descrição da vaga *
                            <textarea name="descricao"
                                rows="4"
                                placeholder="Descreva as atividades da vaga"
                                required></textarea>
                        </label>

                        <button class="botao primario" type="submit">
                            Publicar vaga
                        </button>

                        <p id="mensagem-vaga" class="mensagem"></p>
                    </form>
                `
            : `
                    <p class="vazio">
                        Cadastre uma empresa antes de publicar uma vaga.
                    </p>
                `
    }
        </section>

        <section class="secao">
            <h2>Candidatos cadastrados</h2>

            <label class="busca">
                Buscar formação ou competência
                <input id="busca-candidatos"
                    placeholder="Ex.: Python, Física, Java">
            </label>

            <div id="lista-candidatos"></div>
        </section>
    `);

    // Gráfico de competências
    const contagem = new Map<string, number>();

    lista.forEach((candidato) => {
        candidato.competencias.forEach((competencia) => {
            const chave = competencia.trim();

            if (chave) {
                contagem.set(
                    chave,
                    (contagem.get(chave) ?? 0) + 1
                );
            }
        });
    });

    const competenciasOrdenadas = [...contagem.entries()]
        .sort((a, b) => b[1] - a[1]);

    const canvas = document.querySelector<HTMLCanvasElement>(
        "#grafico-competencias"
    );

    if (canvas && competenciasOrdenadas.length > 0) {
        grafico = new Chart(canvas, {
            type: "bar",
            data: {
                labels: competenciasOrdenadas.map(([nome]) => nome),
                datasets: [{
                    label: "Quantidade de candidatos",
                    data: competenciasOrdenadas.map(([, quantidade]) => quantidade),
                    backgroundColor: "#7865d8",
                    borderRadius: 6
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: { display: false },
                    tooltip: {
                        callbacks: {
                            label: (context) =>
                                `${context.parsed.y} candidato(s)`
                        }
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        ticks: {
                            precision: 0,
                            stepSize: 1
                        }
                    }
                }
            }
        });
    } else if (canvas) {
        canvas.parentElement!.innerHTML =
            "<p>Ainda não existem competências cadastradas.</p>";
    }

    // Cadastro de vagas
    const formularioVaga =
        document.querySelector<HTMLFormElement>("#form-vaga");

    formularioVaga?.addEventListener("submit", (evento) => {
        evento.preventDefault();

        const formulario = evento.currentTarget as HTMLFormElement;
        const dados = new FormData(formulario);

        const vaga: Vaga = {
            id: gerarId(),
            empresaId: String(dados.get("empresaId")),
            titulo: String(dados.get("titulo")).trim(),
            local: String(dados.get("local")).trim(),
            competencias: String(dados.get("competencias"))
                .split(",")
                .map(item => item.trim())
                .filter(Boolean),
            descricao: String(dados.get("descricao")).trim()
        };

        criarVaga(vaga);

        formulario.reset();

        const mensagem = document.querySelector("#mensagem-vaga");
        if (mensagem) {
            mensagem.textContent = "Vaga publicada com sucesso!";
        }
    });

    // Busca e exibição dos candidatos
    const busca = document.querySelector<HTMLInputElement>(
        "#busca-candidatos"
    )!;

    const atualizar = (): void => {
        const consulta = busca.value.toLocaleLowerCase("pt-BR");

        const filtrados = listarCandidatos().filter((candidato) => {
            const texto = [
                candidato.formacao,
                candidato.estado,
                ...candidato.competencias
            ].join(" ").toLocaleLowerCase("pt-BR");

            return texto.includes(consulta);
        });

        const container = document.querySelector<HTMLDivElement>(
            "#lista-candidatos"
        )!;

        container.innerHTML = filtrados.length
            ? `
                <div class="tabela-responsiva">
                    <table>
                        <thead>
                            <tr>
                                <th>Perfil</th>
                                <th>Formação</th>
                                <th>Competências</th>
                                <th>Estado</th>
                                <th>Ação</th>
                            </tr>
                        </thead>
                        <tbody>
                            ${filtrados.map((candidato) => `
                                <tr>
                                    <td>
                                        Candidato anônimo
                                        <small>Identidade protegida</small>
                                    </td>
                                    <td>${escapar(candidato.formacao)}</td>
                                    <td>
                                        <div class="competencias">
                                            ${candidato.competencias.map(skill =>
                `<span>${escapar(skill)}</span>`
            ).join("")}
                                        </div>
                                    </td>
                                    <td>${escapar(candidato.estado)}</td>
                                    <td>
                                        <button
                                            class="botao-excluir"
                                            data-excluir-candidato="${escapar(candidato.id)}"
                                            type="button">
                                            Excluir
                                        </button>
                                    </td>
                                </tr>
                            `).join("")}
                        </tbody>
                    </table>
                </div>
            `
            : `<p class="vazio">Nenhum candidato encontrado.</p>`;
    };

    busca.addEventListener("input", atualizar);
    atualizar();
}


// Navegação e exclusões

app.addEventListener("click", (evento) => {
    const alvo = evento.target as HTMLElement;

    const botaoTela = alvo.closest<HTMLElement>("[data-tela]");

    if (botaoTela) {
        evento.preventDefault();
        navegar(botaoTela.dataset.tela as Tela);
        return;
    }

    const botaoExcluir = alvo.closest<HTMLElement>(
        "[data-excluir-candidato]"
    );

    if (botaoExcluir?.dataset.excluirCandidato) {
        const confirmar = window.confirm(
            "Deseja excluir este candidato?"
        );

        if (confirmar) {
            excluirCandidato(botaoExcluir.dataset.excluirCandidato);
            perfilEmpresa();
        }
    }
});

navegar("inicio");
