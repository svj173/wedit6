# wedit6
WordEditor for story writers.

Редактор текстов для писателя.
Позволяет вести несколько Сборников книг.

Можно
- переносить главы (подглавы) из одного места в другое (в том числе и в другую книгу)
- конвертировать книгу (выбранные главы) в FB2, RTF, DOCX, HTML
- импортировать книгу из DOC, TXT
- и многое другое
                                                
компиляция и создание Редактора из исходников:

ant -buildfile wedit6.xml deploy.linux.locale

ant -Djava.home=/usr/lib/jvm/jdk1.8.0_241 -buildfile wedit6.xml deploy.linux.locale

Внешний вид

![](/home/svj/projects/SVJ/GitHub/wedit6/doc/img/title.png)

Change

2025-октябрь
Отпочковал ветку 1.8 - для создания проекта в java-1.8 - из-за swing.
Отладил компиляцию через ant - он не все видел, что видела Idea.

