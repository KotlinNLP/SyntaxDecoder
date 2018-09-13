# SyntaxDecoder ![No Maintenance Intended](http://unmaintained.tech/badge.svg) [![GitHub version](https://badge.fury.io/gh/KotlinNLP%2FSyntaxDecoder.svg)](https://badge.fury.io/gh/KotlinNLP%2FSyntaxDecoder) [![Build Status](https://travis-ci.org/KotlinNLP/SyntaxDecoder.svg?branch=master)](https://travis-ci.org/KotlinNLP/SyntaxDecoder)

Deprecated. This module will not be maintained nor imported by other KotlinNLP modules. 

SyntaxDecoder is a generalized transition-based parsing framework designed to simplify the development of 
statistical transition-based dependency parsers.

SyntaxDecoder is part of [KotlinNLP](http://kotlinnlp.com/ "KotlinNLP").


## Introduction

SyntaxDecoder provides a unified framework to describe *states* as well as a set of *transitions* that lead the 
system from one state to the next.
It allows to compare various transition-based algorithms from both a theoretical and empirical perspective.

The package includes well-known transition systems (e.g. ArcStandard, ArcHybrid, EasyFirst) but also novel 
unstudied systems. Different types of oracle (*static*, *non-deterministic*, *dynamic*) are implemented for some of 
them.


## Getting Started

### Import with Maven

```xml
<dependency>
    <groupId>com.kotlinnlp</groupId>
    <artifactId>syntaxdecoder</artifactId>
    <version>0.2.3</version>
</dependency>
```


## License

This software is released under the terms of the
[Mozilla Public License, v. 2.0](https://mozilla.org/MPL/2.0/ "Mozilla Public License, v. 2.0")


## Contributions

We greatly appreciate any bug reports and contributions, which can be made by filing an issue or making a pull
request through the [github page](https://github.com/kotlinnlp/SyntaxDecoder "KotlinNLP - SyntaxDecoder on 
GitHub").
