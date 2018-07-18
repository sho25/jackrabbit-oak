begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|index
operator|.
name|lucene
package|;
end_package

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
import|;
end_import

begin_comment
comment|/**  * Facade for possible requests to be done to Lucene, like queries, spellchecking requests, etc..  *  * @param<T> the actual Lucene class representing the request / use case.  */
end_comment

begin_class
class|class
name|LuceneRequestFacade
parameter_list|<
name|T
parameter_list|>
block|{
specifier|private
specifier|final
name|T
name|luceneRequest
decl_stmt|;
name|LuceneRequestFacade
parameter_list|(
annotation|@
name|NotNull
name|T
name|luceneRequest
parameter_list|)
block|{
name|this
operator|.
name|luceneRequest
operator|=
name|luceneRequest
expr_stmt|;
block|}
name|T
name|getLuceneRequest
parameter_list|()
block|{
return|return
name|luceneRequest
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|luceneRequest
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

