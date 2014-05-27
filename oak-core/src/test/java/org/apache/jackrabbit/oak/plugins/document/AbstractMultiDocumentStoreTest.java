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
name|plugins
operator|.
name|document
package|;
end_package

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractMultiDocumentStoreTest
extends|extends
name|AbstractDocumentStoreTest
block|{
specifier|protected
name|DocumentStore
name|ds1
decl_stmt|,
name|ds2
decl_stmt|;
specifier|public
name|AbstractMultiDocumentStoreTest
parameter_list|(
name|DocumentStoreFixture
name|dsf
parameter_list|)
block|{
name|super
argument_list|(
name|dsf
argument_list|)
expr_stmt|;
name|this
operator|.
name|ds1
operator|=
name|super
operator|.
name|ds
expr_stmt|;
name|this
operator|.
name|ds2
operator|=
name|dsf
operator|.
name|createDocumentStore
argument_list|(
literal|2
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

