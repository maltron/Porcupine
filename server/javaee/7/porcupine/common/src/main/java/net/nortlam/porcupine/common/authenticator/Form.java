/**
 * Copyright 2014 Mauricio "Maltron" Leal <maltron@gmail.com>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.nortlam.porcupine.common.authenticator;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.inject.Qualifier;

/**
 * Indicates a authentication using a standard Java EE authentication will be
 * used in order to get access to a certain content. The form must contain
 * the following HTML input fields: j_username, j_password
 * The form must be submitted to: j_security_check
 * 
 * Example of the form used:
 * <html>
 *     <head>
 *         <title>Porcupine: Authorization Server OAuth 2.0</title>
 *         <meta charset="UTF-8">
 *         <meta name="viewport" content="width=device-width, initial-scale=1.0">
 *     </head>
 *     <body>
 *         <div>Porcupine: Authorization Server OAuth 2.0</div>
 *         <div>
 *             <form method="POST" action="j_security_check">
 *                 Email:<input type="text" name="j_username"/>
 *                 Password:<input type="password" name="j_password"/>
 *                 <input type="submit" value="Login"/>
 *                 <input type="reset" value="Reset"/>
 *             </form>
 *         </div>
 *     </body>
 * </html>
 *
 * @author Mauricio "Maltron" Leal */
@Qualifier
@Retention(RUNTIME)
@Target({METHOD, FIELD, PARAMETER, TYPE})
public @interface Form {
}
