# Buildopoli

**Buildopoli** is a library for developing Monopoli based games.

## Features

The library is written in **scala** and provides the user with an environment in which functional programming is blended with object oriented design to achieve flexibility and extensibility.

Buildopoli offers the following main features and abstractions:

* A **set of terrains** where the **player** can place **pawns** on **terrains** via moves after launching **dice**;

* An easy deployable basic game configuration, starting from **Game Session**. Configurable via **Game Options**;

* Extensions to handle **players**, **turns**, **terrains**,**ending conditions**, **events** and **behaviours**;

* Different and configurable **Terrains** like purchasable, buildable or card terrains;

* An interacting simple example with a CLI view already implemented that enables the user to display the status of the game he just created and with whom is possible to interact among events that occurs on different turns and terrains.

## Requirements

To use the library you need the following software installed:

* Scala version 3.2.0
* Sbt version 1.7.1

An IDE is strongly recommended.

## How to use

### Download

Get the latest Jar from the GitHub releases page.

### Import

Open a project in IntelliJ IDEA, then from the the main menu, select **File | Project Structure | Project Settings | Modules**.

Select the module for which you want to add a library and click on the **Dependencies** tab.

Click the **Add** button and select **JARs or directories...**.

Provide the downloaded Jar.

If you what the documentation and source code you can right click on the library, select **Edit... | Add**.

Select from GitHub releases page the sources Jar.

In the **Choose Category and ...** pop-up menu, select **Sources** and press **OK** button.

### Code

Import buildopoli and you're ready to code!

## Examples
