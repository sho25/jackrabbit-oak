begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|api
operator|.
name|jmx
package|;
end_package

begin_interface
specifier|public
interface|interface
name|IndexStatsMBean
block|{
name|String
name|TYPE
init|=
literal|"IndexStats"
decl_stmt|;
name|String
name|STATUS_INIT
init|=
literal|"init"
decl_stmt|;
name|String
name|STATUS_RUNNING
init|=
literal|"running"
decl_stmt|;
name|String
name|STATUS_DONE
init|=
literal|"done"
decl_stmt|;
comment|/**      * @return The time the indexing job stared at, or<code>""</code> if it is      *         not currently running.      */
specifier|public
name|String
name|getStart
parameter_list|()
function_decl|;
comment|/**      * @return The time the indexing job finished at, or<code>""</code> if it      *         is still running.      */
specifier|public
name|String
name|getDone
parameter_list|()
function_decl|;
comment|/**      * Returns the current status of the indexing job      *       * @return the current status of the indexing job: {@value #STATUS_INIT},      *         {@value #STATUS_RUNNING} or {@value #STATUS_DONE}      */
specifier|public
name|String
name|getStatus
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

