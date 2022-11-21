import React, { useEffect, useState } from 'react';
import AdminModeratorOverview from './components/AdminModeratorOverview';
import M from 'materialize-css';
import axios from "axios";


export default function AdminModeratorEdit() {
    const [moderators, setModerators] = useState([
        {
            "name": "NewModerator",
            "user-id": 69
        },
        {
            "name": "Max",
            "user-id": 78
        },
        {
            "name": "Jack",
            "user-id": 79
        },
        {
            "name": "Lux",
            "user-id": 80
        },
        {
            "name": "Sivir",
            "user-id": 81
        }
    ]
    );

    useEffect(() => {
        M.AutoInit();
        getModeratorList();
    });

    function getModeratorList() {
        var config = {
            method: 'get',
            url: '/admin/moderators',
            headers: {}
        };

        axios(config)
            .then(function (response) {
                console.log(JSON.stringify(response.data));
                setModerators(response.data.moderators)
            })
            .catch(function (error) {
                console.log(error);
            });
    }

    function createModerator() {
        var data = JSON.stringify(
            {
                "create-moderator":
                {
                    "name": "Sätt värde från input",
                    "email": "Sätt värde från input",
                    "password": "Sätt värde från input"
                }
            }
        );

        var config = {
            method: 'post',
            url: '/admin/create/moderator',
            headers: {
                'Content-Type': 'text/plain'
            },
            data: data
        };

        axios(config)
            .then(function (response) {
                console.log(JSON.stringify(response.data));
            })
            .catch(function (error) {
                console.log(error);
            });
    }

    return (<>
        <div className='white'>
            <p>Översikt på moderatorer</p>
            <ul className='collapsible'>
                {[...moderators].map((moderator) => (
                    <AdminModeratorOverview key={moderator['user-id']} data={moderator} />
                ))}
            </ul>
        </div>
    </>
    )
}
