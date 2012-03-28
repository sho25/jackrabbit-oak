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
package|;
end_package

begin_comment
comment|/**  * Main exception thrown by methods defined on the {@code Connection} interface  * indicating that commiting a given set of changes failed.  *  * TODO: define mechanism to inform the oak-jcr level about the specific type of exception  *       possible ways:  *       - CommitFailedException contains nested jcr exceptions  *       - CommitFailedException extends from repository exception  *       - CommitFailedException transports status code that are then converted to jcr exceptions  */
end_comment

begin_class
specifier|public
class|class
name|CommitFailedException
extends|extends
name|Exception
block|{ }
end_class

end_unit

