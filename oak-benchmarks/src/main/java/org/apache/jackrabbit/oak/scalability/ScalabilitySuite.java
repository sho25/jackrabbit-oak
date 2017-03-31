begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|scalability
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|fixture
operator|.
name|RepositoryFixture
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|scalability
operator|.
name|benchmarks
operator|.
name|ScalabilityBenchmark
import|;
end_import

begin_comment
comment|/**  * Interface for scalability suite for load testing.  *   * {@link ScalabilitySuite} implementations would configure different {@link ScalabilityBenchmark}  * implementations for executing performance tests and measuring the execution times on those tests.  *   * The entry method for the starting the tests is {@link #run(Iterable)}.  *   */
end_comment

begin_interface
specifier|public
interface|interface
name|ScalabilitySuite
block|{
comment|/**      * Adds the benchmarks to run.      *      * @param benchmarks      * @return      */
name|ScalabilitySuite
name|addBenchmarks
parameter_list|(
name|ScalabilityBenchmark
modifier|...
name|benchmarks
parameter_list|)
function_decl|;
name|boolean
name|removeBenchmark
parameter_list|(
name|String
name|benchmark
parameter_list|)
function_decl|;
name|void
name|run
parameter_list|(
name|Iterable
argument_list|<
name|RepositoryFixture
argument_list|>
name|fixtures
parameter_list|)
function_decl|;
name|Map
argument_list|<
name|String
argument_list|,
name|ScalabilityBenchmark
argument_list|>
name|getBenchmarks
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

