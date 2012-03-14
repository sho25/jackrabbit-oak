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
name|jcr
operator|.
name|util
package|;
end_package

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Item
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|RepositoryException
import|;
end_import

begin_comment
comment|/**  *<code>LogUtil</code>...  */
end_comment

begin_class
specifier|public
class|class
name|LogUtil
block|{
specifier|private
specifier|static
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|LogUtil
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**      * Avoid instantiation      */
specifier|private
name|LogUtil
parameter_list|()
block|{}
comment|/**      * Failsafe retrieval of the JCR path for a given item. This is intended      * to be used in log output, error messages etc.      *      * @param item The target item.      * @return The JCR path of that item or some implementation specific      * string representation of the item.      */
specifier|public
specifier|static
name|String
name|safeGetJCRPath
parameter_list|(
name|Item
name|item
parameter_list|)
block|{
try|try
block|{
return|return
name|item
operator|.
name|getPath
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Failed to retrieve path from item."
argument_list|)
expr_stmt|;
comment|// return string representation of the item as a fallback
return|return
name|item
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

