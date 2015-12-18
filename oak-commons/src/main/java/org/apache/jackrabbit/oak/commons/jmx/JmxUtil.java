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
name|commons
operator|.
name|jmx
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|MalformedObjectNameException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|ObjectName
import|;
end_import

begin_comment
comment|/**  * Utility methods related to JMX  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|JmxUtil
block|{
comment|/**      * Checks if the passed value string can be used as is as part of      * JMX {@link javax.management.ObjectName} If it cannot be used then      * it would return a quoted string which is then safe to be used      * as part of ObjectName.      *      *<p>This is meant to avoid unnecessary quoting of value</p>      *      * @param unquotedValue to quote if required      * @return passed value or quoted value if required      */
specifier|public
specifier|static
name|String
name|quoteValueIfRequired
parameter_list|(
name|String
name|unquotedValue
parameter_list|)
block|{
name|String
name|result
decl_stmt|;
name|String
name|quotedValue
init|=
name|ObjectName
operator|.
name|quote
argument_list|(
name|unquotedValue
argument_list|)
decl_stmt|;
comment|//Check if some chars are escaped or not. In that case
comment|//length of quoted string (excluding quotes) would differ
if|if
condition|(
name|quotedValue
operator|.
name|substring
argument_list|(
literal|1
argument_list|,
name|quotedValue
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
operator|.
name|equals
argument_list|(
name|unquotedValue
argument_list|)
condition|)
block|{
name|ObjectName
name|on
init|=
literal|null
decl_stmt|;
try|try
block|{
comment|//Quoting logic in ObjectName does not escape ',', '='
comment|//etc. So try now by constructing ObjectName. If that
comment|//passes then value can be used as safely
comment|//Also we cannot just rely on ObjectName as it treats
comment|//*, ? as pattern chars and which should ideally be escaped
name|on
operator|=
operator|new
name|ObjectName
argument_list|(
literal|"dummy"
argument_list|,
literal|"dummy"
argument_list|,
name|unquotedValue
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MalformedObjectNameException
name|ignore
parameter_list|)
block|{
comment|//ignore
block|}
if|if
condition|(
name|on
operator|!=
literal|null
condition|)
block|{
name|result
operator|=
name|unquotedValue
expr_stmt|;
block|}
else|else
block|{
name|result
operator|=
name|quotedValue
expr_stmt|;
block|}
block|}
else|else
block|{
comment|//Some escaping done. So do quote
name|result
operator|=
name|quotedValue
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

