interface Resp {
    status: number,
    body: any | undefined,
}

export async function fetchAPI(url: string, method: string, reqBody?: any, getBody: boolean = true): Promise<Resp> {

    try {
        const resp = await fetch(url, {
            method: method,
            redirect: 'manual',
            credentials: "include",
            headers: {
                "content-type": "application/json",
                "Accept": "application/vnd.siren+json, application/json"
            },
            body: JSON.stringify(reqBody)
        })
        if (resp.type == "opaqueredirect") {
            return {
                status: 0,
                body: null
            }
        }
        switch (resp.status) {
            case 200: 
            case 201:
            {
                const body = getBody ? await resp.json() : null
                return {
                    status: resp.status,
                    body: body
                }
                break
            }
            case 400: {
                // TODO: Improve 400 errors messages
                return {
                    status: resp.status,
                    body: null
                }
                break
            }
            case 401: {
                // TODO: Logout in case of 401
                alert("Not logged!")
                break
            }
            default: {
                // TODO: Improve error show
                alert("ERROR default!")
                break
            }
        }
    } catch(err) {
        // TODO: Improve error display
        alert("ERROR catch!")
    }
}
