/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder.utils

import java.util.concurrent.Semaphore

/**
 * A Thread that runs as daemon, waiting to process an input and producing an output.
 *
 * This class must be extended implementing the [processInput] method, that is called every time a new input is written
 * in the [inputValue] (calling the [write] method). The [processInput] should replace the [outputValue] with the new
 * processed value, which will be returned called the [read] method.
 *
 * If an input is written with the [write] method then then the processing starts and the [read] method blocks until the
 * output is available.
 *
 * The [write] method blocks until the last output is read.
 *
 * @param debug a Boolean indicating whether to run the Daemon in debug mode (print logs)
 */
abstract class DaemonThread<InputType: Any, OutputType: Any>(private val debug: Boolean = false) : Thread() {

  /**
   * The reading semaphore.
   */
  private val readSem = Semaphore(1)

  /**
   * The writing semaphore.
   */
  private val writeSem = Semaphore(1)

  /**
   * The processing semaphore.
   */
  private val processSem = Semaphore(1)

  /**
   * The input value.
   */
  protected lateinit var inputValue: InputType

  /**
   * The output value.
   */
  protected lateinit var outputValue: OutputType

  /**
   * Reading and processing are blocked by default.
   */
  init {
    this.readSem.acquire()
    this.processSem.acquire()
  }

  /**
   * The main Thread run method.
   * An infinite loop that processes inputs.
   */
  override fun run() {

    while (true) {

      try {
        this.process()

      } catch (e: InterruptedException) {
        break
      }
    }
  }

  /**
   * Read an output from this thread.
   *
   * @return the output value
   */
  fun read(): OutputType {

    if (this.debug) println("${this.id}: Waiting for reading output")
    this.readSem.acquire()

    val ret = this.outputValue

    if (this.debug) println("${this.id}: Output read, release input writing")
    this.writeSem.release()

    return ret
  }

  /**
   * Write an input into this thread.
   *
   * @param value the input value
   */
  fun write(value: InputType) {

    if (this.debug) println("${this.id}: Waiting for writing input")
    this.writeSem.acquire()

    this.inputValue = value

    if (this.debug) println("${this.id}: Input written, release processing")
    this.processSem.release()
  }

  /**
   * Call the [processInput] managing semaphores.
   */
  private fun process() {

    if (this.debug) println("${this.id}: Waiting for processing")
    this.processSem.acquire()

    this.processInput()

    if (this.debug) println("${this.id}: Data processed, release output reading")
    this.readSem.release()
  }

  /**
   * Called every time a new input is written into this thread.
   * Process it and produce a new output.
   */
  protected abstract fun processInput()
}
