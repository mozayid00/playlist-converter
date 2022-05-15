function submitForm() {
    const sourcePlaylistElement = document.getElementById("source-playlist-type");
    const sourcePlaylistValue = sourcePlaylistElement.options[sourcePlaylistElement.selectedIndex].value;
    const destinationPlaylistElement = document.getElementById("destination-playlist-type");
    const destinationPlaylistValue = destinationPlaylistElement.options[destinationPlaylistElement.selectedIndex].value;
    const sourcePlaylistUrl = document.getElementById("playlist-url").value;

    fetch(`convert?url=${sourcePlaylistUrl}&starting=${sourcePlaylistValue}&destination=${destinationPlaylistValue}`)
        .then(function(res) {
            if (!res.ok){
                throw new Error('Request failed.');
            }
            res.json()
                .then(function(data){
                    document.getElementById("result").innerHTML =
                        `Playlist converted successfully. <a href="${data.playlistUrl}">Click here to view playlist.</a>`;
                })
        })
        .catch(function(err) {
            console.error(err);
            document.getElementById("result").innerHTML = 'Error attempting to convert playlist. Please try again.';
        });
}