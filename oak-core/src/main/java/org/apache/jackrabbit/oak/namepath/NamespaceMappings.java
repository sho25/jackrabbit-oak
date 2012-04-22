begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/* * Licensed to the Apache Software Foundation (ASF) under one or more * contributor license agreements.  See the NOTICE file distributed with * this work for additional information regarding copyright ownership. * The ASF licenses this file to You under the Apache License, Version 2.0 * (the "License"); you may not use this file except in compliance with * the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
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
name|namepath
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * Prove of concept implementation for OAK-61.  *  * This implementation is entirely in memory. TODO: persist mappings  *  * For each registered mapping from a jcr prefix to a namespace a  * a mk prefix is generated. The mk prefixes are in one to one relation  * ship with the registered namespaces and should be used as shorthands  * in place of the actual namespaces in all further name and path handling.  *  * TODO: expose the relevant methods through the Oak API.  */
end_comment

begin_class
specifier|public
class|class
name|NamespaceMappings
block|{
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|jcr2NsMap
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|ns2MkMap
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|mk2JcrMap
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
comment|/**      * Add a mapping jcr prefix to namespace mapping. If either      * {@code jcrPrefix} or {@code namespace} is already mapped, the      * existing mapping is removed first.      *      * @param jcrPrefix      * @param namespace      */
specifier|public
name|void
name|registerNamespace
parameter_list|(
name|String
name|jcrPrefix
parameter_list|,
name|String
name|namespace
parameter_list|)
block|{
if|if
condition|(
name|jcrPrefix
operator|==
literal|null
operator|||
name|namespace
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|()
throw|;
block|}
name|unregisterJcrPrefix
argument_list|(
name|jcrPrefix
argument_list|)
expr_stmt|;
name|unregisterNamespace
argument_list|(
name|namespace
argument_list|)
expr_stmt|;
name|String
name|mk
init|=
name|ns2MkMap
operator|.
name|get
argument_list|(
name|namespace
argument_list|)
decl_stmt|;
if|if
condition|(
name|mk
operator|==
literal|null
condition|)
block|{
comment|// Generate a mk prefix. Use jcrPrefix if possible, disambiguate otherwise
name|mk
operator|=
name|jcrPrefix
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|2
init|;
name|ns2MkMap
operator|.
name|containsValue
argument_list|(
name|mk
argument_list|)
condition|;
name|i
operator|++
control|)
block|{
name|mk
operator|=
name|jcrPrefix
operator|+
name|i
expr_stmt|;
block|}
name|ns2MkMap
operator|.
name|put
argument_list|(
name|namespace
argument_list|,
name|mk
argument_list|)
expr_stmt|;
block|}
name|mk2JcrMap
operator|.
name|put
argument_list|(
name|mk
argument_list|,
name|jcrPrefix
argument_list|)
expr_stmt|;
name|jcr2NsMap
operator|.
name|put
argument_list|(
name|jcrPrefix
argument_list|,
name|namespace
argument_list|)
expr_stmt|;
block|}
comment|/**      * Remove the mapping of {@code jcrPrefix} if such exists.      * @param jcrPrefix      * @return  the namespace to which {@code jcrPrefix} mapped or      * {@code null} if none.      */
specifier|public
name|String
name|unregisterJcrPrefix
parameter_list|(
name|String
name|jcrPrefix
parameter_list|)
block|{
if|if
condition|(
name|jcrPrefix
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|()
throw|;
block|}
name|String
name|ns
init|=
name|jcr2NsMap
operator|.
name|remove
argument_list|(
name|jcrPrefix
argument_list|)
decl_stmt|;
name|mk2JcrMap
operator|.
name|remove
argument_list|(
name|ns2MkMap
operator|.
name|get
argument_list|(
name|ns
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|ns
return|;
block|}
comment|/**      * Remove the mapping for {@code namespace} if such exists.      * @param namespace      * @return  the jcr prefix which mapped to {@code namespace} or      * {@code null} if none.      */
specifier|public
name|String
name|unregisterNamespace
parameter_list|(
name|String
name|namespace
parameter_list|)
block|{
if|if
condition|(
name|namespace
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|()
throw|;
block|}
name|String
name|jcrPrefix
init|=
name|mk2JcrMap
operator|.
name|remove
argument_list|(
name|ns2MkMap
operator|.
name|get
argument_list|(
name|namespace
argument_list|)
argument_list|)
decl_stmt|;
name|jcr2NsMap
operator|.
name|remove
argument_list|(
name|jcrPrefix
argument_list|)
expr_stmt|;
return|return
name|jcrPrefix
return|;
block|}
comment|/**      * Retrieve the namespace which {@code jcrPrefix} maps to if any      * or {@code null} otherwise.      * @param jcrPrefix      * @return  namespace or {@code null}      */
specifier|public
name|String
name|getNamespace
parameter_list|(
name|String
name|jcrPrefix
parameter_list|)
block|{
return|return
name|jcr2NsMap
operator|.
name|get
argument_list|(
name|jcrPrefix
argument_list|)
return|;
block|}
comment|/**      * Retrieve the jcr prefix which maps to {@code namespace} if any      * or {@code null} otherwise.      * @param namespace      * @return  jcr prefix or {@code null}      */
specifier|public
name|String
name|getJcrPrefix
parameter_list|(
name|String
name|namespace
parameter_list|)
block|{
return|return
name|mk2JcrMap
operator|.
name|get
argument_list|(
name|ns2MkMap
operator|.
name|get
argument_list|(
name|namespace
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Return the registered namespaces      * @return      */
specifier|public
name|String
index|[]
name|getNamespaces
parameter_list|()
block|{
return|return
name|jcr2NsMap
operator|.
name|values
argument_list|()
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|jcr2NsMap
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
comment|/**      * Return the registered jcr prefixes      * @return      */
specifier|public
name|String
index|[]
name|getJcrPrefixes
parameter_list|()
block|{
return|return
name|jcr2NsMap
operator|.
name|keySet
argument_list|()
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|jcr2NsMap
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
comment|//------------------------------------------------------------< internal>---
comment|/**      * Retrieve the jcr prefix which maps to {@code mkPrefix} if any      * or {@code null} otherwise.      * @param mkPrefix      * @return  jcr prefix or {@code null}      */
name|String
name|getJcrPrefixFromMk
parameter_list|(
name|String
name|mkPrefix
parameter_list|)
block|{
return|return
name|mk2JcrMap
operator|.
name|get
argument_list|(
name|mkPrefix
argument_list|)
return|;
block|}
comment|/**      * Retrieve the mk prefix which maps to {@code jcrPrefix} if any      * or {@code null} otherwise.      * @param jcrPrefix      * @return  mk prefix or {@code null}      */
name|String
name|getMkPrefixFromJcr
parameter_list|(
name|String
name|jcrPrefix
parameter_list|)
block|{
return|return
name|ns2MkMap
operator|.
name|get
argument_list|(
name|jcr2NsMap
operator|.
name|get
argument_list|(
name|jcrPrefix
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

